package com.example.webtooninfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.rxjava3.EmptyResultSetException
import com.example.webtooninfo.database.FavouritesDatabase
import com.example.webtooninfo.database.FavouritesEntity
import com.example.webtooninfo.ui.theme.WebtoonInfoTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil3.compose.AsyncImage as AsyncImage

class DetailedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebtoonInfoTheme {
               val intent:Intent=intent
                val id=intent.getIntExtra("id",0)

                display(id = id)
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun display(id:Int){
        val details= alltitles()[id-1]
        val name=details.name
        val photo=details.photo
        val reads=details.reads
        val brief=details.brief
        val content=details.content
        var rating by remember {
            mutableFloatStateOf(0.0f)
        }
        var totalRatings  by remember {
            mutableIntStateOf(0)
        }
        var sumRating by remember {
            mutableIntStateOf(0)
        }
        val db = FirebaseDatabase.getInstance().getReference("Ratings").child(id.toString())
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    sumRating = snapshot.child("sumRating").value?.toString()?.toIntOrNull() ?: 0
                    totalRatings = snapshot.child("totalRatings").value?.toString()?.toIntOrNull() ?: 0
                    rating=(sumRating.toFloat())/ (totalRatings.toFloat())
                } else {
//                    Toast.makeText(this@DetailedActivity, "No ratings found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@DetailedActivity, "Error in ratings: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        Column(
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .padding(top = 50.dp)
//                    .fillMaxHeight(0.4f)
                    .fillMaxWidth(1f)
            ){
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    textAlign = TextAlign.Center
                )
                AsyncImage(
                    model = photo,
                    contentDescription = "photo",
                    modifier = Modifier
                        .padding(20.dp)
                        // .shadow(elevation = 10.dp, shape = RoundedCornerShape(35.dp))
                        .border(
                            5.dp,
                            color = Color(22, 139, 179, 37),
                            shape = RoundedCornerShape(35.dp)
                        )
                        .clip(shape = RoundedCornerShape(35.dp))
                        .fillMaxWidth(1f)
//                    .height(200.dp)
                        .clickable { },
                    contentScale = ContentScale.Fit
                )
                Text(
                    "$brief \n Rating: $rating/5 ($totalRatings)",
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 8.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(
                modifier= Modifier
                    .fillMaxHeight(0.6f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    content,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 8.dp, bottom = 8.dp),
                    textAlign = TextAlign.Left,
                )
            }
            var favouriteTitle: FavouritesEntity? =null
            var isFavourite by remember {
                mutableStateOf(favouriteTitle!=null)
            }
            isFavourite(applicationContext, id) { favouriteEntity ->
                if (favouriteEntity != null) {
                    // The favourite item exists, you can access its properties
                    favouriteTitle=favouriteEntity
                    isFavourite=true
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = {
                        val intent=Intent(this@DetailedActivity,RatingActivity::class.java)
                        intent.putExtra("id",id)
                        startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .height(50.dp) // Adjust height as needed
                        .align(Alignment.CenterHorizontally)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        text = "Rate here",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                if(isFavourite){
                    Button(
                        onClick = {
//                            if (favouriteTitle != null) {
                            val title:FavouritesEntity=FavouritesEntity(id,name,photo,brief,reads,content)
                                remove(applicationContext, title)
//                            }
                            isFavourite=false
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height(50.dp) // Adjust height as needed
                            .align(Alignment.CenterHorizontally)
                            .width(200.dp),
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(
                            text = "Remove From Fav",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else{
                    Button(
                        onClick = {
                                val title:FavouritesEntity=FavouritesEntity(id,name,photo,brief,reads,content)
                                addToFav(applicationContext,title)
                                isFavourite=true
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height(50.dp) // Adjust height as needed
                            .align(Alignment.CenterHorizontally)
                            .width(200.dp),
                        colors =  ButtonDefaults.buttonColors(Color.Gray),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(
                            text = "Add to Favorites",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
        fun isFavourite(context: Context, id: Int,onResult: (FavouritesEntity?) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = Room.databaseBuilder(context, FavouritesDatabase::class.java, "favourites-db").build()
                val favourite = db.FavouritesDao().getFavouriteById(id)
                withContext(Dispatchers.Main) {
                    onResult(favourite) // Callback on the main thread with the result
                }
            }
        }
        fun addToFav(context: Context, title: FavouritesEntity) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = Room.databaseBuilder(context, FavouritesDatabase::class.java, "favourites-db").build()
                db.FavouritesDao().insertTitle(title)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    fun remove(context: Context,favouriteTitle:FavouritesEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(context, FavouritesDatabase::class.java, "favourites-db").build()
            db.FavouritesDao().deleteTitle(favouriteTitle)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
