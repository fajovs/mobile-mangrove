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

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Bakawan', 'Rhizophora apiculata Blume', 'Maypangdan to Bugas, Borongan City, Eastern Samar', 'Rhizophora is a genus of tropical mangrove trees, sometimes collectively called true mangroves. -Wikipedia.com\n" +
                        "- Etymology: The genus name Rhizophora derives from Greek words rhiza, meaning \"root\", and phoros, meaning \"bearing\", referring to the stilt-roots. The species epithet apiculata derives from Latin, meaning \"to end abruptly\", referring to the leaf apex.\n" +
                        "- There are eight bakauan species in the genus Rhizophora. Three are found in the Philippines: R. mucronata (bakauan babae), R. stylosa\n" +
                        "(bakauan bato or bangkao), and\n" +
                        "R. apiculata (bakauan lalaki). There may be a fourth, bakauan hybrid pula, first sighted  by C. E, Yao in the Visayas in 1984. Further sightings in Bohol confirmed a report by mangrove expert Fred Vande Vusse who believes the species could be Rhizophora x lamarckii, a sterile hybrid between bakauan lalaki and bakauan bato. - stuartxchange.org')")

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Pototan', 'Bruguiera gymnorrhiza', 'San Saturnino to Bugas, Borongan City, Eastern Samar', 'Pototan is shared by Pototan-lalaki (Bruguiera cylindrica, White Burma mangrove), Karandang (Bruguiera sexangula) and Pototan (Bruguiera gymnorrhiza, Black mangrove). This is not uncommon in Philippine nomenclatura of plant names of plants; perhaps, a lackadaisical regional adoption of common names. - stuartxchange.org')")

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Miyapi', 'Avicennia officinalis Linn', 'Camada to Bugas, Borongan City, Eastern Samar', 'Description: Avicennia officinalis, also known as Api Api Ludat, is a mangrove tree. It has large orange-yellow flowers that smell rancid. The leaves are oblong shaped and the underside are distinctly yellowish green. -nparks.gov.sg/florafaunaweb/flora/3/2/3267')")

                db.execSQL("INSERT INTO plant (name, scientificName, location, description) VALUES ('Pagatpat', 'Sonneratia acida L.f.','Camada to Bugas, Borongan City, Eastern Samar', 'Sonneratia is a genus of plants in the family Lythraceae, formerly placed in the family Sonneratiaceae which included both Sonneratia and Duabanga, both of which are not placed in their own monotypic subfamilies of the family Lythraceae. -inaturalist.org Etymology: The genus name Sonneratia honors the French botanist and explorer, Pierre Sonnerat (1749-1841). The species epithet derives from Latin caseolaris, meaning \"small cheese\", likely referring to the cheese-like taste imparted by the ripe fruit. -stuartxchange.org')")


            } catch (e: Exception) {
                Log.e("AppDatabaseCallback", "Error populating database: ", e)
            }
        }
    }
}
