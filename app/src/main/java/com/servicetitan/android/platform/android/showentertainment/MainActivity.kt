package com.servicetitan.android.platform.android.showentertainment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.mutableStateOf
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.res.imageResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.servicetitan.android.platform.android.showentertainment.api.ShowApiProvider
import com.servicetitan.android.platform.android.showentertainment.api.model.Show
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

private val IMAGE_URL = "https://image.tmdb.org/t/p/w300/"
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    var showApiRepository = ShowApiProvider.showApiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPopularShows()
    }

    private fun requestPopularShows() {
        lifecycleScope.launch {
            showApiRepository.popularShow()
                .onStart { showLoading() }
                .combine(showApiRepository.genre()) { shows, genre ->
                    shows.forEach { it.updateGenre(genre) }.let { shows }
                }.handleErrors(TAG)
                .collect {
                    updateView(it)
                }
        }
    }

    private fun updateView(shows: List<Show>) {
        setContent {
            MaterialTheme {
                Shows(shows = shows)
            }
        }
    }

    @Composable
    fun Shows(shows: List<Show>) {
        VerticalScroller(modifier = Modifier.padding(8.dp)) {
            Column {
                for (show in shows) {
                    ShowCard(show)
                    Divider(modifier = Modifier.padding(4.dp), color = Color.White)
                }
            }
        }
    }

    @Composable
    fun ShowCard(show: Show) {
        Clickable(modifier = Modifier.ripple(), onClick = {
            startActivity(navigateToDetails(this, show.id))
        }) { ShowCardContent(show) }
    }

    @Composable
    private fun generateImage(url: String?): MutableState<ImageAsset> {
        val imageAsset: MutableState<ImageAsset> = mutableStateOf(ImageAsset(1, 1))
        Glide.with(ContextAmbient.current)
            .asBitmap()
            .load(IMAGE_URL + url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageAsset.value = resource.asImageAsset()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }

            })
        return imageAsset
    }

    @Composable
    fun ShowCardContent(show: Show) {
        Card(shape = MaterialTheme.shapes.medium, color = Color.White) {
            Column {
                Image(
                    asset = generateImage(url = show.backdropPath).value,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.preferredHeight(120.dp).fillMaxSize()
                )

                Stack(
                    modifier = Modifier.fillMaxSize()
                        .padding(vertical = 2.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = show.name,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.gravity(Alignment.TopStart)
                    )
                    Text(
                        text = show.voteAverage.toString(),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.gravity(Alignment.TopEnd)
                    )
                }

                Text(
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
                    text = show.overview ?: "",
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2
                )

                Stack(
                    modifier = Modifier.fillMaxSize()
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = show.genreList.joinToString { it.name ?: "" },
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.gravity(Alignment.TopStart).width(250.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = show.firstAirDate ?: "",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.gravity(Alignment.BottomEnd)
                    )
                }
            }
        }
    }

    private fun showLoading() {
        setContent {
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                CircularProgressIndicator()
            }
        }
    }
}