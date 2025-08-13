package com.karate.kime.screens

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

private const val TAG = "YouTubeWebPlayer"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeWebPlayer(
    videoId: String,
    modifier: Modifier = Modifier,
    onError: ((String) -> Unit)? = null
) {
    if (videoId.isBlank()) {
        val msg = "VideoId vazio"
        Log.e(TAG, msg)
        onError?.invoke(msg)
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Vídeo não disponível")
        }
        return
    }

    val html = remember(videoId) {
        """
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            <style>body,html{margin:0;padding:0;background:black;height:100%} .video-container{position:relative;width:100%;height:100%} iframe{position:absolute;top:0;left:0;width:100%;height:100%;border:0}</style>
          </head>
          <body>
            <div class="video-container">
              <iframe
                src="https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1&playsinline=1&controls=1"
                frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen>
              </iframe>
            </div>
          </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.allowContentAccess = true
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        super.onReceivedError(view, request, error)
                        val url = request?.url?.toString().orEmpty()
                        val desc = error?.description?.toString().orEmpty()
                        val msg = "onReceivedError url=$url code=${error?.errorCode} desc=$desc"
                        Log.e(TAG, msg)
                        onError?.invoke(msg)
                    }

                    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        val url = request?.url?.toString().orEmpty()
                        val status = errorResponse?.statusCode ?: -1
                        val reason = errorResponse?.reasonPhrase ?: ""
                        val msg = "onReceivedHttpError url=$url status=$status reason=$reason"
                        Log.e(TAG, msg)
                        onError?.invoke(msg)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d(TAG, "page finished: $url")
                        view?.evaluateJavascript(
                            "(function(){try{ return document.getElementsByTagName('iframe')[0] != null; }catch(e){return false;}})();"
                        ) { result ->
                            if (result == "false") {
                                val msg = "iframe não disponível/foi bloqueado pelo provedor"
                                Log.e(TAG, msg)
                                onError?.invoke(msg)
                            }
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                }

                loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
            }
        }
    )
}
