package com.bernaferrari.sdkmonitor.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Brand marks for the About links, as tintable vectors (single-color logos on a 256 grid). */
object BrandIcons {
    val GitHub: ImageVector get() = cachedGitHub ?: brand("GitHub", GITHUB_PATH, PathFillType.NonZero).also { cachedGitHub = it }
    private var cachedGitHub: ImageVector? = null

    private const val GITHUB_PATH =
        "M208.31,75.68A59.78,59.78 0,0 0,202.93 28,8 8,0 0,0 196,24a59.75,59.75 0,0 0,-48 24H124A59.75,59.75 0,0 0,76 24a8,8 0,0 0,-6.93 4,59.78 59.78,0 0,0 -5.38,47.68A58.14,58.14 0,0 0,56 104v8a56.06,56.06 0,0 0,48.44 55.47A39.8,39.8 0,0 0,96 192v8H72a24,24 0,0 1,-24 -24A40,40 0,0 0,8 136a8,8 0,0 0,0 16,24 24,0 0,1 24,24 40,40 0,0 0,40 40H96v16a8,8 0,0 0,16 0V192a24,24 0,0 1,48 0v40a8,8 0,0 0,16 0V192a39.8,39.8 0,0 0,-8.44 -24.53A56.06,56.06 0,0 0,216 112v-8A58.14,58.14 0,0 0,208.31 75.68ZM200,112a40,40 0,0 1,-40 40H112a40,40 0,0 1,-40 -40v-8a41.74,41.74 0,0 1,6.9 -22.48A8,8 0,0 0,80 73.83a43.81,43.81 0,0 1,0.79 -33.58,43.88 43.88,0 0,1 32.32,20.06A8,8 0,0 0,119.82 64h32.35a8,8 0,0 0,6.74 -3.69,43.87 43.87,0 0,1 32.32,-20.06A43.81,43.81 0,0 1,192 73.83a8.09,8.09 0,0 0,1 7.65A41.72,41.72 0,0 1,200 104Z"

    val LinkedIn: ImageVector get() = cachedLinkedIn ?: brand("LinkedIn", LINKEDIN_PATH, PathFillType.NonZero).also { cachedLinkedIn = it }
    private var cachedLinkedIn: ImageVector? = null

    private const val LINKEDIN_PATH =
        "M216,24L40,24A16,16 0,0 0,24 40L24,216a16,16 0,0 0,16 16L216,232a16,16 0,0 0,16 -16L232,40A16,16 0,0 0,216 24ZM216,216L40,216L40,40L216,40L216,216ZM96,112v64a8,8 0,0 1,-16 0L80,112a8,8 0,0 1,16 0ZM184,140v36a8,8 0,0 1,-16 0L168,140a20,20 0,0 0,-40 0v36a8,8 0,0 1,-16 0L112,112a8,8 0,0 1,15.79 -1.78A36,36 0,0 1,184 140ZM100,84A12,12 0,1 1,88 72,12 12,0 0,1 100,84Z"

    val Reddit: ImageVector get() = cachedReddit ?: brand("Reddit", REDDIT_PATH, PathFillType.NonZero).also { cachedReddit = it }
    private var cachedReddit: ImageVector? = null

    private const val REDDIT_PATH =
        "M248,104a32,32 0,0 0,-52.94 -24.19c-16.75,-8.9 -36.76,-14.28 -57.66,-15.53l5.19,-31.17 17.72,2.72a24,24 0,1 0,2.87 -15.74l-26,-4a8,8 0,0 0,-9.11 6.59L121.2,64.16c-21.84,0.94 -42.82,6.38 -60.26,15.65a32,32 0,0 0,-42.59 47.74A59,59 0,0 0,16 144c0,21.93 12,42.35 33.91,57.49C70.88,216 98.61,224 128,224s57.12,-8 78.09,-22.51C228,186.35 240,165.93 240,144a59,59 0,0 0,-2.35 -16.45A32.16,32.16 0,0 0,248 104ZM184,24a8,8 0,1 1,-8 8A8,8 0,0 1,184 24ZM224.13,117.78a8,8 0,0 0,-3.29 10A43.58,43.58 0,0 1,224 144c0,16.53 -9.59,32.27 -27,44.33C178.67,201 154.17,208 128,208s-50.67,-7 -69,-19.67C41.59,176.27 32,160.53 32,144a43.75,43.75 0,0 1,3.14 -16.17,8 8,0 0,0 -3.27,-10A16,16 0,1 1,52.94 94.59a8,8 0,0 0,10.45 2.23l0.36,-0.22C81.45,85.9 104.25,80 128,80h0c23.73,0 46.53,5.9 64.23,16.6l0.42,0.25a8,8 0,0 0,10.39 -2.26,16 16,0 1,1 21.07,23.19ZM88,144a16,16 0,1 1,16 -16A16,16 0,0 1,88 144ZM184,128a16,16 0,1 1,-16 -16A16,16 0,0 1,184 128ZM167.07,172.25a8,8 0,0 1,-3.32 10.82,76.18 76.18,0 0,1 -71.5,0 8,8 0,1 1,7.5 -14.14,60.18 60.18,0 0,0 56.5,0A8,8 0,0 1,167.07 172.25Z"

    val X: ImageVector get() = cachedX ?: brand("X", X_PATH, PathFillType.NonZero).also { cachedX = it }
    private var cachedX: ImageVector? = null

    private const val X_PATH =
        "M214.75,211.71l-62.6,-98.38 61.77,-67.95a8,8 0,0 0,-11.84 -10.76L143.24,99.34 102.75,35.71A8,8 0,0 0,96 32H48a8,8 0,0 0,-6.75 12.3l62.6,98.37 -61.77,68a8,8 0,1 0,11.84 10.76l58.84,-64.72 40.49,63.63A8,8 0,0 0,160 224h48a8,8 0,0 0,6.75 -12.29ZM164.39,208 L62.57,48h29L193.43,208Z"

    /** Google Play's current mark (the four-part triangle), one color; from Simple Icons (CC0). */
    val GooglePlay: ImageVector get() = cachedGooglePlay ?: brand("GooglePlay", GOOGLE_PLAY_PATH, PathFillType.NonZero, 24f).also { cachedGooglePlay = it }
    private var cachedGooglePlay: ImageVector? = null

    private const val GOOGLE_PLAY_PATH =
        "M22.018 13.298l-3.919 2.218-3.515-3.493 3.543-3.521 3.891 2.202a1.49 1.49 0 0 1 0 2.594zM1.337.924a1.486 1.486 0 0 0-.112.568v21.017c0 .217.045.419.124.6l11.155-11.087L1.337.924zm12.207 10.065l3.258-3.238L3.45.195a1.466 1.466 0 0 0-.946-.179l11.04 10.973zm0 2.067l-11 10.933c.298.036.612-.016.906-.183l13.324-7.54-3.23-3.21z"

    private fun brand(name: String, d: String, fillType: PathFillType, viewport: Float = 256f): ImageVector =
        ImageVector
            .Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = viewport, viewportHeight = viewport)
            .addPath(pathData = addPathNodes(d), pathFillType = fillType, fill = SolidColor(Color.Black))
            .build()
}
