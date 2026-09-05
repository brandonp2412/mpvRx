/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package app.gyrolet.mpvrx.ui.player

import org.json.JSONArray
import java.util.Locale

internal fun parseIntentSkipSegments(raw: String?): List<SkipSegment> {
  if (raw.isNullOrBlank()) return emptyList()
  return runCatching {
    val array = JSONArray(raw)
    buildList {
      for (index in 0 until array.length()) {
        val item = array.optJSONObject(index) ?: continue
        val start = item.optDouble("start", Double.NaN)
        val end = item.optDouble("end", Double.NaN)
        if (!start.isFinite() || !end.isFinite() || start < 0.0 || end <= start) continue
        val type = when (item.optString("type").lowercase(Locale.ROOT)) {
          "intro" -> SkipSegmentType.INTRO
          "recap" -> SkipSegmentType.RECAP
          "outro" -> SkipSegmentType.OUTRO
          "credits" -> SkipSegmentType.CREDITS
          "preview" -> SkipSegmentType.PREVIEW
          else -> continue
        }
        add(SkipSegment(type, start, end, "external-intent"))
      }
    }
  }.getOrElse { emptyList() }
}
