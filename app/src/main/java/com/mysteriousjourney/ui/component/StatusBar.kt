package com.mysteriousjourney.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.ui.theme.Black
import com.mysteriousjourney.ui.theme.DarkGray
import com.mysteriousjourney.ui.theme.GoldPrimary
import com.mysteriousjourney.ui.theme.LightGray
import com.mysteriousjourney.ui.theme.MadnessRed
import com.mysteriousjourney.ui.theme.SpiritBlue
import com.mysteriousjourney.ui.theme.White

@Composable
fun StatusBar(
    spirit: Int,
    maxSpirit: Int,
    madness: Int,
    maxMadness: Int,
    goldPounds: Int,
    soles: Int,
    pence: Int,
    currentTime: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkGray)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentTime,
                color = White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            
            Text(
                text = "$goldPounds GBP $soles shillings $pence pence",
                color = GoldPrimary,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "灵性 $spirit/$maxSpirit",
                    color = SpiritBlue,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = spirit.toFloat() / maxSpirit.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = SpiritBlue,
                    trackColor = LightGray
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "疯狂 $madness/$maxMadness",
                    color = MadnessRed,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = madness.toFloat() / maxMadness.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MadnessRed,
                    trackColor = LightGray
                )
            }
        }
        
        if (onClick != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "点击查看详情 ▼",
                color = GoldPrimary.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
