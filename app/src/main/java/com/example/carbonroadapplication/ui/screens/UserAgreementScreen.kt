package com.example.carbonroadapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAgreementScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("用户协议", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 头部欢迎语
            Text(
                text = "欢迎加入森途大家庭",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "最后更新：2025年1月1日",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 协议卡片列表
            AgreementCard(
                icon = Icons.Rounded.VerifiedUser,
                title = "1. 服务宗旨",
                content = "“森途”致力于利用科技力量推动低碳生活。我们将您的每一次步行、骑行转化为可视化的绿色能量，让环保不再枯燥。"
            )

            AgreementCard(
                icon = Icons.Rounded.LocationOn,
                title = "2. 核心权限说明",
                content = "为了精准记录您的绿色足迹，我们需要使用您的GPS定位权限。请放心，定位服务仅在您点击“出发”后开启，行程结束立即关闭。您的轨迹数据仅保存在本地，绝不上传云端，最大程度保护您的隐私安全。"
            )

            AgreementCard(
                icon = Icons.Rounded.Park,
                title = "3. 虚拟资产与激励",
                content = "应用内的“虚拟树木”、“勋章”及“成长值”是我们为您准备的精神奖励，旨在记录您的环保成就。请注意，这些均为数字化权益，不具备法定的货币价值，不支持提现或交易。"
            )

            AgreementCard(
                icon = Icons.Rounded.Analytics,
                title = "4. 数据算法",
                content = "碳减排量的计算基于通用的环保公式（如：每公里步行约减少xxx克碳排放）。受设备传感器精度影响，里程数据可能存在细微偏差，仅供个人参考与娱乐。"
            )

            AgreementCard(
                icon = Icons.Rounded.Security,
                title = "5. 免责声明",
                content = "作为一款独立开发作品，我们将竭尽全力保障服务稳定性。对于因不可抗力（如网络故障、手机系统问题）导致的数据异常，恳请您的谅解。出行时请务必注意交通安全！"
            )

            AgreementCard(
                icon = Icons.Rounded.Email,
                title = "6. 联系与反馈",
                content = "我们重视每一位用户的声音。如有任何疑问或建议，欢迎随时联系开发者邮箱：songjie2027@126.com"
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 底部Logo
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("ForestWay · 森途", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AgreementCard(icon: ImageVector, title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
        }
    }
}