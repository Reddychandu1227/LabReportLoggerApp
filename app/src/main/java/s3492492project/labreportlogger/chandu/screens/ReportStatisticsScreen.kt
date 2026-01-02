package s3492492project.labreportlogger.chandu.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import s3492492project.labreportlogger.chandu.UserPrefs
import s3492492project.labreportlogger.chandu.ui.theme.crimsonRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportStatisticsScreen(
    onGoToReports: () -> Unit
) {
    val context = LocalContext.current
    val email = UserPrefs.getEmail(context).replace(".", "_")
    val databaseRef = FirebaseDatabase.getInstance().reference

    var isLoading by remember { mutableStateOf(true) }
    var reports by remember { mutableStateOf<List<LabReport>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        databaseRef.child("Myreports")
            .child(email)
            .get()
            .addOnSuccessListener { snapshot ->
                reports = snapshot.children
                    .mapNotNull { it.getValue(LabReport::class.java) }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to load statistics", Toast.LENGTH_SHORT).show()
            }
    }

    val totalReports = reports.size
    val favoriteCount = reports.count { it.isfavorite }

    val categoryStats = reports.groupBy { it.category }
        .mapValues { it.value.size }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lab Report Logger App", fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = crimsonRed,
                    titleContentColor = Color.White,
                )
            )
        }
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            reports.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No reports available",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Text(
                        text = "Overview",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Reports",
                            value = totalReports.toString(),
                            background = Color(0xFFE8F1FF),
                            icon = Icons.Default.Description,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Favorites",
                            value = favoriteCount.toString(),
                            background = Color(0xFFFFF1E0),
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = "Reports by Category",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    categoryStats.forEach { (category, count) ->
                        CategoryStatRow(
                            category = category,
                            count = count
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    InfoSection(
                        title = "About Us",
                        icon = Icons.Default.Info,
                        content = "Laboratory reports can be written, organized and handled digitally using the Lab Report Logger App to provide easy access and submit laboratory reports."
                    )

                    Spacer(Modifier.height(16.dp))

                    InfoSection(
                        title = "Contact Us",
                        icon = Icons.Default.Call,
                        content = "Email: Reddychandu1227@gmail.com"
                    )

                    Spacer(Modifier.height(30.dp))
                }

            }
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    icon: ImageVector,
    content: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFA5A62)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                content,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}


@Composable
fun StatCard(
    title: String,
    value: String,
    background: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF333333),
                modifier = Modifier.size(26.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                title,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Text(
                value,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun CategoryStatRow(
    category: String,
    count: Int
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Label,
                contentDescription = null,
                tint = Color(0xFFFA5A62),
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                category,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )

            Text(
                count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


