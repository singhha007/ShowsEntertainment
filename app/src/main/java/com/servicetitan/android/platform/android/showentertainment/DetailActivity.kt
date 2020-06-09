package com.servicetitan.android.platform.android.showentertainment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.layout.ColumnScope.gravity
import androidx.ui.material.Card
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.res.imageResource
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import com.servicetitan.android.platform.android.showentertainment.api.ShowApiProvider
import com.servicetitan.android.platform.android.showentertainment.api.model.CreatedBy
import com.servicetitan.android.platform.android.showentertainment.api.model.Season
import com.servicetitan.android.platform.android.showentertainment.api.model.ShowDetail
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val KEY_SHOW_ID = "key-show-id"
private const val TAG = "DetailActivity"

fun navigateToDetails(context: Context, showId: Int) =
    Intent(context, DetailActivity::class.java).apply { putExtra(KEY_SHOW_ID, showId) }

class DetailActivity : AppCompatActivity() {

    var disposable = CompositeDisposable()
    var showApiRepository = ShowApiProvider.showApiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        requestShowDetails()
    }

    private fun requestShowDetails() {
        val showId = intent?.getIntExtra(KEY_SHOW_ID, -1) ?: -1

        lifecycleScope.launch {
            showApiRepository.showDetails(showId)
                .onStart { showLoading() }
                .handleErrors(TAG)
                .collect { showMovieDetail(it) }
        }
    }

    private fun showMovieDetail(showDetail: ShowDetail) {
        setContent {
            Column(modifier = Modifier.padding(4.dp)) {
                HeaderContent(showDetail)
                Spacer(modifier = Modifier.fillMaxWidth().height(8.dp))
                BodyContent(showDetail)
            }
        }
    }

    @Composable
    private fun HeaderContent(showDetail: ShowDetail) {
        Row(modifier = Modifier.fillMaxWidth().preferredHeight(200.dp)) {
            Image(
                asset = imageResource(R.drawable.placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier.preferredWidth(200.dp)
                    .clip(shape = MaterialTheme.shapes.medium).weight(0.4f)
            )
            Column(modifier = Modifier.weight(0.6f).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                    text = showDetail.name,
                    style = MaterialTheme.typography.h5
                        .merge(TextStyle(fontWeight = FontWeight(FontWeight.Bold.weight)))
                )
                Text(text = showDetail.genres.joinToString { it.name ?: "" })
                Text(text = generateDate(showDetail.firstAirDate))

                Divider(
                    modifier = Modifier.padding(4.dp).drawOpacity(0.5f),
                    color = Color.LightGray
                )
                Text(
                    text = showDetail.lastEpisodeToAir.name,
                    style = MaterialTheme.typography.subtitle1.merge(
                        TextStyle(fontWeight = FontWeight(FontWeight.Bold.weight))
                    )
                )
                Text(text = generateDate(showDetail.lastAirDate))
                Text(text = "Season: ${showDetail.lastEpisodeToAir.seasonNumber} Episode: ${showDetail.lastEpisodeToAir.episodeNumber}")
                Text(text = "${showDetail.episodeRunTime.first()} minutes runtime on ${showDetail.networks.first().name}")
                Text(
                    text = "Rated: ${showDetail.voteAverage}",
                    style = MaterialTheme.typography.subtitle1.merge(
                        TextStyle(fontWeight = FontWeight(FontWeight.Bold.weight))
                    )
                )
            }
        }
    }

    @Composable
    private fun BodyContent(showDetail: ShowDetail) {
        VerticalScroller(modifier = Modifier.padding(4.dp)) {
            Column {
                Divider(
                    modifier = Modifier.padding(4.dp).drawOpacity(0.5f),
                    color = Color.LightGray
                )

                Text(
                    text = "Creators",
                    style = MaterialTheme.typography.subtitle2.merge(TextStyle(color = Color.LightGray))
                )
                HorizontalScroller(
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 8.dp
                    )
                ) {
                    Row { showDetail.createdBy.forEach { CreatorCard(it) } }
                }

                Divider(
                    modifier = Modifier.padding(4.dp).drawOpacity(0.5f),
                    color = Color.LightGray
                )

                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.subtitle2.merge(TextStyle(color = Color.LightGray))
                )
                Text(text = showDetail.overview, style = MaterialTheme.typography.body1)

                Divider(
                    modifier = Modifier.padding(4.dp).drawOpacity(0.5f),
                    color = Color.LightGray
                )
                Text(
                    text = "Production Companies",
                    style = MaterialTheme.typography.subtitle2.merge(TextStyle(color = Color.LightGray))
                )
                HorizontalScroller(
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 8.dp
                    )
                ) {
                    Row { showDetail.productionCompanies.forEach { ProductionCompanyCard(name = it.name) } }
                }

                Divider(
                    modifier = Modifier.padding(4.dp).drawOpacity(0.5f),
                    color = Color.LightGray
                )

                Divider(
                    modifier = Modifier.padding(4.dp).drawOpacity(0.5f),
                    color = Color.LightGray
                )

                Text(
                    text = "Seasons",
                    style = MaterialTheme.typography.subtitle2.merge(TextStyle(color = Color.LightGray))
                )
                VerticalScroller(modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
                    Column {
                        showDetail.seasons.filter { it.airDate != null }.forEach { SeasonCard(it) }
                    }
                }
            }
        }
    }

    private fun generateDate(date: String) =
        LocalDate.parse(date)
            .let { it.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) }

    @Composable
    fun ProductionCompanyCard(name: String) {
        Card(border = Border(1.dp, Color.LightGray), shape = CircleShape) {
            Text(text = name, modifier = Modifier.gravity(Alignment.CenterHorizontally).padding(12.dp),
            style = MaterialTheme.typography.subtitle1)
        }
        Spacer(modifier = Modifier.width(8.dp))
    }

    @Composable
    fun CreatorCard(createdBy: CreatedBy) {
        Card(border = Border(1.dp, Color.LightGray)) {
            Column(modifier = Modifier.padding(8.dp)) {
                Box(
                    modifier = Modifier.preferredSize(35.dp).gravity(Alignment.CenterHorizontally),
                    backgroundColor = Color.Black,
                    shape = CircleShape
                )
                Text(text = createdBy.name, modifier = Modifier.gravity(Alignment.CenterHorizontally).padding(top = 4.dp))
            }
        }
        Divider(modifier = Modifier.padding(4.dp))
    }

    @Composable
    private fun SeasonCard(it: Season) {
        Card(border = Border(1.dp, Color.LightGray), elevation = 4.dp) {
            Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                Stack(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.gravity(
                            Alignment.CenterStart
                        )
                    )
                    Text(
                        text = generateDate(it.airDate ?: ""),
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.gravity(
                            Alignment.CenterEnd
                        )
                    )
                }
                Text(text = "Episodes: ${it.episodeCount}", style = MaterialTheme.typography.body1)
                Text(text = it.overview, style = MaterialTheme.typography.body1)
            }
        }
        Divider(modifier = Modifier.padding(4.dp))
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