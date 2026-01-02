package s3492492project.labreportlogger.chandu.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import s3492492project.labreportlogger.chandu.UserPrefs
import s3492492project.labreportlogger.chandu.ui.theme.crimsonRed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(
    onViewReport: (LabReport) -> Unit
) {
    val context = LocalContext.current
    val email = UserPrefs.getEmail(context).replace(".", "_")
    val databaseRef = FirebaseDatabase.getInstance().reference

    var reports by remember { mutableStateOf<List<LabReport>>(emptyList()) }
    var filteredReports by remember { mutableStateOf<List<LabReport>>(emptyList()) }

    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All Categories") }

    LaunchedEffect(Unit) {
        isLoading = true

        databaseRef.child("Myreports").child(email)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.children
                    .mapNotNull { it.getValue(LabReport::class.java) }

                reports = list.reversed()
                filteredReports = reports
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to load reports", Toast.LENGTH_SHORT).show()
            }
    }


    val categories = remember(reports) {
        listOf("All Categories") + reports.map { it.category }.distinct()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Reports", fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = crimsonRed,
                    titleContentColor = Color.White,
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter by Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false

                                filteredReports = if (category == "All Categories")
                                    reports
                                else
                                    reports.filter { it.category == category }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if(isLoading)
            {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }else if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No reports found", fontSize = 18.sp, color = Color.Gray)
                }
            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(filteredReports) { report ->

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(22.dp),
                            elevation = CardDefaults.elevatedCardElevation(8.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color.White
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                ElevatedCard(
                                    shape = RoundedCornerShape(18.dp),
                                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                                    modifier = Modifier.size(76.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = Color(0xFFF3F3F3)
                                    )
                                ) {
                                    val thumbnail = report.images.firstOrNull()
                                    if (thumbnail != null) {
                                        Image(
                                            painter = rememberAsyncImagePainter(thumbnail),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = Color(0xFFFA5A62),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = report.title,
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF222222),
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // Category
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = null,
                                            tint = Color(0xFF777777),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = report.category,
                                            fontSize = 14.sp,
                                            color = Color(0xFF777777)
                                        )
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    // Date
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = Color(0xFF999999),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = report.date,
                                            fontSize = 13.sp,
                                            color = Color(0xFF999999)
                                        )
                                    }
                                }

                                // 🔹 View Button with Icon
                                ElevatedButton(
                                    onClick = { onViewReport(report) },
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "View",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }


                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    MaterialTheme {
        MyReportsScreen(onViewReport = {})
    }
}


@Composable
fun ReportDetailsRouteHandler(
    reportId: String,
    onBack: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val email = UserPrefs.getEmail(context).replace(".", "_")
    val db = FirebaseDatabase.getInstance().reference

    var report by remember { mutableStateOf<LabReport?>(null) }

    LaunchedEffect(reportId) {
        db.child("Myreports")
            .child(email)
            .child(reportId)
            .get()
            .addOnSuccessListener { snapshot ->
                report = snapshot.getValue(LabReport::class.java)
            }
    }

    if (report == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        ReportDetailsScreen(
            report = report!!,
            onBack = onBack,
            navController
        )
    }
}