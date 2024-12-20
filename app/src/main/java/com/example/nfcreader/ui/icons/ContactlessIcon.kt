package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.AutoMirrored.ContactlessIcon: ImageVector
    get() {
        if (_contactlessIcon != null) {
            return _contactlessIcon!!
        }
        _contactlessIcon = materialIcon(name = "Filled.Contactless") {
            materialPath {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(8.46f, 14.45f)
                lineTo(7.0f, 13.0f)
                curveToRelative(0.89f, -0.94f, 2.14f, -1.5f, 3.5f, -1.5f)
                reflectiveCurveToRelative(2.61f, 0.56f, 3.5f, 1.5f)
                lineToRelative(-1.46f, 1.45f)
                curveToRelative(-0.56f, -0.56f, -1.31f, -0.88f, -2.04f, -0.88f)
                reflectiveCurveToRelative(-1.48f, 0.32f, -2.04f, 0.88f)
                close()
                moveTo(12.0f, 8.5f)
                curveToRelative(2.3f, 0.0f, 4.37f, 0.94f, 5.87f, 2.45f)
                lineToRelative(-1.46f, 1.45f)
                curveTo(15.21f, 11.21f, 13.69f, 10.5f, 12.0f, 10.5f)
                reflectiveCurveToRelative(-3.21f, 0.71f, -4.41f, 1.9f)
                lineToRelative(-1.46f, -1.45f)
                curveTo(7.63f, 9.44f, 9.7f, 8.5f, 12.0f, 8.5f)
                close()
                moveTo(12.0f, 4.5f)
                curveToRelative(3.72f, 0.0f, 7.08f, 1.51f, 9.51f, 3.95f)
                lineTo(20.05f, 9.9f)
                curveToRelative(-2.02f, -2.02f, -4.81f, -3.27f, -7.9f, -3.27f)
                reflectiveCurveToRelative(-5.88f, 1.25f, -7.9f, 3.27f)
                lineTo(2.79f, 8.45f)
                curveTo(5.22f, 6.01f, 8.58f, 4.5f, 12.0f, 4.5f)
                close()
            }
        }
        return _contactlessIcon!!
    }

private var _contactlessIcon: ImageVector? = null 