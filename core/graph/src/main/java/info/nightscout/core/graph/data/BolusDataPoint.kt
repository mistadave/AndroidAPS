package info.nightscout.core.graph.data

import android.content.Context
import info.nightscout.core.graph.R
import info.nightscout.database.entities.Bolus
import info.nightscout.interfaces.plugin.ActivePlugin
import info.nightscout.interfaces.profile.DefaultValueHelper
import info.nightscout.interfaces.utils.DecimalFormatter
import info.nightscout.shared.interfaces.ResourceHelper

class BolusDataPoint(
    val data: Bolus,
    private val rh: ResourceHelper,
    private val activePlugin: ActivePlugin,
    private val defaultValueHelper: DefaultValueHelper
) : DataPointWithLabelInterface {

    private var yValue = 0.0

    override fun getX(): Double = data.timestamp.toDouble()
    override fun getY(): Double = if (data.type == Bolus.Type.SMB) defaultValueHelper.determineLowLine() else yValue
    override val label
        get() = DecimalFormatter.toPumpSupportedBolus(data.amount, activePlugin.activePump, rh)
    override val duration = 0L
    override val size = 2f

    override val shape
        get() = if (data.type == Bolus.Type.SMB) PointsWithLabelGraphSeries.Shape.SMB else PointsWithLabelGraphSeries.Shape.BOLUS

    override fun color(context: Context?): Int =
        if (data.type == Bolus.Type.SMB) rh.gac(context, R.attr.smbColor)
        else if (data.isValid) rh.gac(context, R.attr.bolusDataPointColor)
        else rh.gac(context, R.attr.alarmColor)

    override fun setY(y: Double) {
        yValue = y
    }
}