package android.friedrich.sudoKu.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.constraintlayout.widget.Constraints.TAG;

@Database(entities = {Puzzle.class, Cell.class}, version = 1, exportSchema = true)
public abstract class PuzzleRoomDatabase extends RoomDatabase {
    public abstract PuzzleDao puzzleDao();
    public abstract CellDao cellDao();

    private static volatile PuzzleRoomDatabase INSTANCE;
    public static final String NAME_OF_DATABASE_FILE = "puzzle_database";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService sDatabaseExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static PuzzleRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PuzzleRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PuzzleRoomDatabase.class, NAME_OF_DATABASE_FILE)
                            .addCallback(sRoomDataCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDataCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            sDatabaseExecutor.execute(new Runnable() {
                @Override
                public void run() {
                        PuzzleDao puzzleDao = INSTANCE.puzzleDao();
                        puzzleDao.deleteAll();
                        CellDao cellDao = INSTANCE.cellDao();
                        cellDao.deleteAll();
                }
            });
        }
    };
}
