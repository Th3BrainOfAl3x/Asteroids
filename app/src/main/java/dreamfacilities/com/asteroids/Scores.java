package dreamfacilities.com.asteroids;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

/**
 * Created by alex on 18/10/16.
 */

public class Scores extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new MyAdapter(this,
                MainMenuActivity.scoresStorage.scoresList(10));

        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                String s = MainMenuActivity.scoresStorage.scoresList(10).get(pos);
                Toast.makeText(Scores.this, "Selección: " + pos
                        + " - " + s, Toast.LENGTH_LONG).show();
            }
        });

    }
}