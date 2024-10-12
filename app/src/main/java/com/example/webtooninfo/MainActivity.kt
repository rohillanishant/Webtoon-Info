package com.example.webtooninfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.CalendarContract.Colors
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import coil3.compose.AsyncImage
import com.example.webtooninfo.database.FavouritesDatabase
import com.example.webtooninfo.database.FavouritesEntity
import com.example.webtooninfo.ui.theme.WebtoonInfoTheme
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val items = listOf(
                BottomNavigationItem(
                    title = "Home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                ),
                BottomNavigationItem(
                    title = "Favourites",
                    selectedIcon = Icons.Filled.Favorite,
                    unselectedIcon = Icons.Outlined.FavoriteBorder
                )
            )
            var selectedItemIndex by rememberSaveable {
                mutableIntStateOf(0)
            }
            WebtoonInfoTheme {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItemIndex == index,
                                    onClick = {
                                        selectedItemIndex = index
                                    },
                                    label = {
                                        Text(text = item.title)
                                    },
                                    alwaysShowLabel = false,
                                    icon = {
                                        BadgedBox(
                                            badge = {

                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (index == selectedItemIndex) {
                                                    item.selectedIcon
                                                } else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) {
                    if(selectedItemIndex==0) Home()
                    else Favourites()
                }
            }
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun Home(){
        var toon by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier
                .background(Color(94, 205, 243, 37))
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .padding(top = 20.dp),
            Arrangement.Top,
            Alignment.CenterHorizontally
        ){
            TextField(
                value = toon,
                onValueChange = {
                    toon=it
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search User",
                        modifier = Modifier.padding(start=10.dp)
                    )
                },
                trailingIcon = {
                    if(toon!=""){
                        IconButton(onClick = { toon=""}) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.padding(start=10.dp)
                            )
                        }
                    }
                },
                placeholder = { Text(text = "Search") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor =Color(94, 205, 243, 37),
                    unfocusedContainerColor = Color(94, 205, 243, 37)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 20.dp, bottom = 10.dp)
            )
            val allTitles= alltitles()
            val filteredTitles = allTitles.filter { it.name.startsWith(toon, ignoreCase = true) || it.id.toString().startsWith(toon, ignoreCase = true)}
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .verticalScroll(rememberScrollState())) {
                if(filteredTitles.isEmpty()){
                    Text(
                        text = "Title Not found",
                        color = Color.LightGray,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 90.dp),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )
                }
                for(i in filteredTitles){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent=Intent(this@MainActivity,DetailedActivity::class.java)
                                intent.putExtra("id",i.id)
                                startActivity(intent)
                            }
                            .padding(10.dp),
//                        .background(Color(94, 205, 243, 37)),
                        shape = RoundedCornerShape(30.dp),
                        colors = CardColors(
                            containerColor = Color(94, 205, 243, 37),
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                        ) {
                            Text(
                                "${i.id}. ${i.name}",
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(top = 8.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                                    .shadow(shape = RoundedCornerShape(25.dp), elevation = 0.dp)
                            ) {
                                AsyncImage(
                                    model = i.photo,
                                    contentDescription = "photo",
                                    modifier = Modifier
                                        // .shadow(elevation = 10.dp, shape = RoundedCornerShape(35.dp))
                                        .border(
                                            5.dp,
                                            color = Color(22, 139, 179, 37),
                                            shape = RoundedCornerShape(35.dp)
                                        )
                                        .clip(shape = RoundedCornerShape(35.dp))
                                        .fillMaxWidth(0.9f)
                                        .height(200.dp)
                                        .clickable {
                                            val intent=Intent(this@MainActivity,DetailedActivity::class.java)
                                            intent.putExtra("id",i.id)
                                            startActivity(intent)
                                        },
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Text(
                                i.brief,
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(top = 8.dp, bottom = 8.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun Favourites(){
        var toon by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier
                .background(Color(94, 205, 243, 37))
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .padding(top = 20.dp),
            Arrangement.Top,
            Alignment.CenterHorizontally
        ){
            TextField(
                value = toon,
                onValueChange = {
                    toon=it
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search User",
                        modifier = Modifier.padding(start=10.dp)
                    )
                },
                trailingIcon = {
                    if(toon!=""){
                        IconButton(onClick = { toon=""}) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.padding(start=10.dp)
                            )
                        }
                    }
                },
                placeholder = { Text(text = "Search") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor =Color(94, 205, 243, 37),
                    unfocusedContainerColor = Color(94, 205, 243, 37)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 20.dp, bottom = 10.dp)
            )

            val favouriteTitles= RetrieveFavourites(applicationContext).execute().get()
            val filteredTitles = favouriteTitles.filter { it.name.startsWith(toon, ignoreCase = true) || it.id.toString().startsWith(toon, ignoreCase = true)}
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .verticalScroll(rememberScrollState())) {
                if(filteredTitles.isEmpty()){
                    Text(
                        text = "Title Not found",
                        color = Color.LightGray,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 90.dp),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )
                }
                for(i in filteredTitles){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent=Intent(this@MainActivity,DetailedActivity::class.java)
                                intent.putExtra("id",i.id)
                                startActivity(intent)
                            }
                            .padding(10.dp),
//                        .background(Color(94, 205, 243, 37)),
                        shape = RoundedCornerShape(30.dp),
                        colors = CardColors(
                            containerColor = Color(94, 205, 243, 37),
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                        ) {
                            Text(
                                "${i.id}. ${i.name}",
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(top = 8.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                                    .shadow(shape = RoundedCornerShape(25.dp), elevation = 0.dp)
                            ) {
                                AsyncImage(
                                    model = i.photo,
                                    contentDescription = "photo",
                                    modifier = Modifier
                                        // .shadow(elevation = 10.dp, shape = RoundedCornerShape(35.dp))
                                        .border(
                                            5.dp,
                                            color = Color(22, 139, 179, 37),
                                            shape = RoundedCornerShape(35.dp)
                                        )
                                        .clip(shape = RoundedCornerShape(35.dp))
                                        .fillMaxWidth(0.9f)
                                        .height(200.dp)
                                        .clickable {
                                            val intent=Intent(this@MainActivity,DetailedActivity::class.java)
                                            intent.putExtra("id",i.id)
                                            startActivity(intent)
                                        },
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Text(
                                i.brief,
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(top = 8.dp, bottom = 8.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<FavouritesEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<FavouritesEntity> {
            val db= databaseBuilder(context, FavouritesDatabase::class.java,"favourites-db").build()
            return db.FavouritesDao().getAllFavourites()
        }

    }
}



