@file:Suppress("DEPRECATION")

package app.aaps.wear.complications

import android.app.PendingIntent
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationText
import app.aaps.core.interfaces.logging.LTag
import app.aaps.wear.data.RawDisplayData
import app.aaps.wear.interaction.utils.DisplayFormat
import app.aaps.wear.interaction.utils.SmallestDoubleString
import dagger.android.AndroidInjection
import kotlin.math.max

/*
 * Created by dlvoy on 2019-11-12
 */
class BrCobIobComplication : BaseComplicationProviderService() {

    // Not derived from DaggerService, do injection here
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun buildComplicationData(dataType: Int, raw: RawDisplayData, complicationPendingIntent: PendingIntent): ComplicationData? {
        var complicationData: ComplicationData? = null
        if (dataType == ComplicationData.TYPE_SHORT_TEXT) {
            val cob = SmallestDoubleString(raw.status[0].cob, SmallestDoubleString.Units.USE).minimise(DisplayFormat.MIN_FIELD_LEN_COB)
            val iob = SmallestDoubleString(raw.status[0].iobSum, SmallestDoubleString.Units.USE).minimise(max(DisplayFormat.MIN_FIELD_LEN_IOB, DisplayFormat.MAX_FIELD_LEN_SHORT - 1 - cob.length))
            val builder = ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                .setShortText(ComplicationText.plainText(displayFormat.basalRateSymbol() + raw.status[0].currentBasal))
                .setShortTitle(ComplicationText.plainText("$cob $iob"))
                .setTapAction(complicationPendingIntent)
            complicationData = builder.build()
        } else {
            aapsLogger.warn(LTag.WEAR, "Unexpected complication type $dataType")
        }
        return complicationData
    }

    override fun getProviderCanonicalName(): String = BrCobIobComplication::class.java.canonicalName!!
}
