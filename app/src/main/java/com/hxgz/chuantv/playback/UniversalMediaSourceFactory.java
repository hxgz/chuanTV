package com.hxgz.chuantv.playback;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.*;
import com.google.android.exoplayer2.util.Util;
import com.hxgz.chuantv.utils.LogUtil;

import java.io.File;

class UniversalMediaSourceFactory {
    /**
     * Data source factory of this app
     */
    private final DataSource.Factory dataSourceFactory;

    /**
     * Initialize the Universal MediaSource Factory
     *
     * @param context   the context to create the factory in
     * @param userAgent the useragent to use when streaming media
     */
    UniversalMediaSourceFactory(Context context, String userAgent) {
        //create http data source factory that allows http -> https redirects
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);

        dataSourceFactory = new DefaultDataSourceFactory(context, httpDataSourceFactory);
    }

    /**
     * Create a MediaSource from the given uri
     *
     * @param uri the uri to create the media source from
     * @return the media source created, or null if the media type is NOT supported
     */
    MediaSource createMediaSource(Uri uri) {
        //create MediaSource according to stream type
        switch (Util.inferContentType(uri)) {
            case C.TYPE_DASH: {
                //DASH stream
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            }
            case C.TYPE_HLS: {
                //HLS stream
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            }
            case C.TYPE_SS: {
                //SmoothStreaming stream
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            }
            case C.TYPE_OTHER: {
                //Progressive stream
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            }
            default: {
                //invalid = type not supported
                LogUtil.d("Cannot create MediaSource from uri " + uri.toString());
                return null;
            }
        }
    }
}
