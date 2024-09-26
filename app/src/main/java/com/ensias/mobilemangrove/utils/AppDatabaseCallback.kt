package com.ensias.mobilemangrove.utils

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        scope.launch {
            populateDatabase(db)
        }
    }

    private suspend fun populateDatabase(db: SupportSQLiteDatabase) {
        withContext(Dispatchers.IO) {
            try {

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Bakawan', 'Rhizophora apiculata Blume', 'Maypangdan to Bugas', 'Rhizophora is a genus of tropical mangrove trees, sometimes collectively called true mangroves.\n" +
                        "- Etymology: The genus name Rhizophora derives from Greek words rhiza, meaning \"root\", and phoros, meaning \"bearing\", referring to the stilt-roots. The species epithet apiculata derives from Latin, meaning \"to end abruptly\", referring to the leaf apex.')")

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Pototan', 'Bruguiera gymnorrhiza', 'San Saturnino to bugas', 'Pototan is shared by Pototan-lalaki (Bruguiera cylindrica, White Burma mangrove), Karandang (Bruguiera sexangula) and Pototan (Bruguiera gymnorrhiza, Black mangrove). This is not uncommon in Philippine nomenclatura of plant names of plants; perhaps, a lackadaisical regional adoption of common names.')")

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Miyapi', 'Avicennia officinalis Linn', 'Whole borongan', ' Avicennia officinalis, also known as Api Api Ludat, is a mangrove tree. It has large orange-yellow flowers that smell rancid. The leaves are oblong shaped and the underside are distinctly yellowish green.')")

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Pagatpat', 'Sonneratia acida L.f.', 'Whole borongan', ' Sonneratia is a genus of plants in the family Lythraceae, formerly placed in the family Sonneratiaceae which included both Sonneratia and Duabanga, both of which are not placed in their own monotypic subfamilies of the family Lythraceae.')")

            } catch (e: Exception) {
                Log.e("AppDatabaseCallback", "Error populating database: ", e)
            }
        }
    }
}
