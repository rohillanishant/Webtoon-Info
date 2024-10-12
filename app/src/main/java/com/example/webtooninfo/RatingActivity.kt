package com.example.webtooninfo

import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.webtooninfo.database.FavouritesEntity
import com.example.webtooninfo.ui.theme.WebtoonInfoTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RatingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebtoonInfoTheme {
                val intent: Intent =intent
                val id=intent.getIntExtra("id",0)
                Rating(id)
            }
        }
    }
    @Composable
    fun Rating(id:Int){
        val details= alltitles()[id-1]
        val name=details.name
        val photo=details.photo
        val brief=details.brief
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
                rating=(sumRating.toFloat())/(totalRatings.toFloat())
                Text(
                    "$brief \n Rating: $rating/5 ($totalRatings)",
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 8.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                var phoneNumber by remember {
                    mutableStateOf("")
                }
                var rateValue by remember {
                    mutableStateOf("")
                }
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if(it.length<=10) phoneNumber=it
                    },
                    maxLines = 1,
                    leadingIcon = {Icon(Icons.Default.Phone, contentDescription = "phone")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    label = {
                        Text(text = "Phone Number")
                    },
                    placeholder = {
                        Text(text = "Enter Phone Number")
                    },
                    modifier = Modifier.padding(top=20.dp).align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = rateValue.toString(),
                    onValueChange = {
                        if(it.toInt() in 0..5) rateValue=it
                    },
                    maxLines = 1,
                    leadingIcon = {Icon(Icons.Default.Star, contentDescription = "phone")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    label = {
                        Text(text = "Rating Value")
                    },
                    placeholder = {
                        Text(text = "Enter Value in 1-5")
                    },
                    modifier = Modifier.padding(top=20.dp,bottom=20.dp).align(Alignment.CenterHorizontally)
                )

                Button(
                    onClick = { submit(id,phoneNumber,rateValue.toInt(), sumRating = sumRating,totalRatings)
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
                        text = "Submit Rating",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    fun submit(id:Int,phoneNumber:String,rateValue:Int,sumRating:Int,totalRatings:Int){
        if(phoneNumber.length!=10){
            Toast.makeText(this@RatingActivity, "Enter correct phone number", Toast.LENGTH_SHORT).show()
            return
        }else if(rateValue<1 || rateValue>5){
            Toast.makeText(this@RatingActivity, "Enter rating value in range 1-5", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseDatabase.getInstance().getReference("Ratings").child(id.toString()).child("details").child(phoneNumber)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Phone number exists, now you can access its value (Rating)
                    val oldRating = snapshot.value.toString()
                    val newRating=rateValue.toInt()
                    db.setValue(rateValue)
                    val dbRef=FirebaseDatabase.getInstance().getReference("Ratings").child(id.toString())
                    dbRef.child("sumRating").setValue(sumRating+newRating-oldRating.toInt() )
//                    val db = FirebaseDatabase.getInstance().getReference("Ratings")
//            for(i in 1..50){
//                db.child(i.toString()).child("id").setValue(i)
//                db.child(i.toString()).child("rating").setValue(0.0f)
//                db.child(i.toString()).child("totalRatings").setValue(0)
//            }
                    Toast.makeText(this@RatingActivity, "Updated Rating from $oldRating to $rateValue", Toast.LENGTH_SHORT).show()
                } else {
                    // Phone number does not exist
                    db.setValue(rateValue)
                    val dbRef=FirebaseDatabase.getInstance().getReference("Ratings").child(id.toString())
                    dbRef.child("sumRating").setValue(sumRating+rateValue)
                    dbRef.child("totalRatings").setValue(totalRatings.toInt()+1)

                    Toast.makeText(this@RatingActivity, "Added your rating", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RatingActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
