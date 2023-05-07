package hu.mobilalkfejl.weddingapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import hu.mobilalkfejl.weddingapp.R;
import hu.mobilalkfejl.weddingapp.model.Wedding;
import hu.mobilalkfejl.weddingapp.notification.NotificationController;
import hu.mobilalkfejl.weddingapp.recyclerview.WeddingElementAdapter;

public class WeddingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = WeddingsActivity.class.getName();
    private ArrayList<Wedding> weddings;
    private WeddingElementAdapter weddingElementAdapter;
    private CollectionReference collectionReference;
    private NotificationController notificationController;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weddings);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            finish();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2));

        weddings = new ArrayList<>();

        weddingElementAdapter = new WeddingElementAdapter(this, weddings);
        recyclerView.setAdapter(weddingElementAdapter);

        collectionReference = FirebaseFirestore.getInstance().collection("Weddings");

        notificationController = new NotificationController(this);

        selectWeddings();
    }

    private void createWeddings(CollectionReference collectionReference) {
        this.collectionReference = collectionReference;
    }

    private void selectWeddings() {
        weddings.clear();
        collectionReference.orderBy("name").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Wedding wedding = document.toObject(Wedding.class);
                wedding.setId(document.getId());
                weddings.add(wedding);
            }

            if (weddings.size() == 0) {
                new CreateCollection().execute();
                selectWeddings();
            }

            weddingElementAdapter.notifyDataSetChanged();
        });
    }

    public void reservation(Wedding wedding) {
        new UpdateAvailability().execute(wedding);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationController.sendNotification("Sikeresen lefoglaltad a következő helyszínt: " + wedding.getName());
            return;
        }

        Toast.makeText(this, "Sikeresen lefoglaltad a következő helyszínt: " + wedding.getName(), Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void takePicture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
            return;
        }

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    public void delete(Wedding wedding) {
        new DeleteWedding().execute(wedding);
        Toast.makeText(this, wedding.getName() + " sikeresen törölve lett!", Toast.LENGTH_LONG).show();
    }

    public class CreateCollection extends AsyncTask<Void, Void, CollectionReference> {
        @Override
        protected CollectionReference doInBackground(Void... voids) {
            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Weddings");

            String[] names = getResources().getStringArray(R.array.wedding_names);
            String[] locations = getResources().getStringArray(R.array.wedding_locations);
            TypedArray covers = getResources().obtainTypedArray(R.array.wedding_covers);

            for (int i = 0; i < names.length; i++)
                collectionReference.add(new Wedding(
                        names[i],
                        locations[i],
                        true,
                        covers.getResourceId(i, 0)
                ));

            covers.recycle();
            return collectionReference;
        }

        @Override
        protected void onPostExecute(CollectionReference collectionReference) {
            createWeddings(collectionReference);
        }
    }

    public class UpdateAvailability extends AsyncTask<Wedding, Void, Void> {
        @Override
        protected Void doInBackground(Wedding... weddings) {
            Wedding wedding = weddings[0];
            collectionReference.document(wedding.getId()).update("available", false);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            selectWeddings();
        }
    }

    public class DeleteWedding extends AsyncTask<Wedding, Void, Void> {
        @Override
        protected Void doInBackground(Wedding... weddings) {
            Wedding wedding = weddings[0];

            DocumentReference ref = collectionReference.document(wedding.getId());
            ref.delete();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            selectWeddings();
        }
    }
}