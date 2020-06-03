package com.servicetitan.android.platform.android.showentertainment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.ripple.ripple
import androidx.ui.res.imageResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import com.servicetitan.android.platform.android.showentertainment.api.ShowApiProvider
import com.servicetitan.android.platform.android.showentertainment.api.model.GenreResponse
import com.servicetitan.android.platform.android.showentertainment.api.model.Response
import com.servicetitan.android.platform.android.showentertainment.api.model.Show
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

private val IMAGE_URL = "https://image.tmdb.org/t/p/w500/"
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    var disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPopularShows()
    }

    private fun requestPopularShows() {
        Observable.zip(
            ShowApiProvider.provideShowApi().popularShow(),
            ShowApiProvider.provideShowApi().genre(),
            BiFunction { shows: Response<Show>, genreResponse: GenreResponse ->
                shows.results.forEach { it.updateGenre(genreResponse.genres) }
                shows.results
            }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoading() }
            .subscribe({
                updateView(it)
            }, {
                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }).addTo(disposable)
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
    fun ShowCardContent(show: Show) {
        Card(shape = MaterialTheme.shapes.medium, color = Color.White) {
            Column {
                Image(
                    asset = imageResource(R.drawable.placeholder),
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
                    text = show.overview,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2
                )

                Stack(
                    modifier = Modifier.fillMaxSize()
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = show.genreList.map { it.name }.joinToString(),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.gravity(Alignment.TopStart).width(250.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = show.firstAirDate,
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

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}