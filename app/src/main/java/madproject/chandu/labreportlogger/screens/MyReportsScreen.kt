package madproject.chandu.labreportlogger.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import madproject.chandu.labreportlogger.UserPrefs
import madproject.chandu.labreportlogger.ui.theme.crimsonRed
import kotlin.jvm.java


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

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All Categories") }

    LaunchedEffect(true) {
        databaseRef.child("Myreports").child(email)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.children.mapNotNull { it.getValue(LabReport::class.java) }
                reports = list.reversed()
                filteredReports = reports
            }
    }

    // Extract unique categories
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

            // ---------------- FILTER DROPDOWN ----------------
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

            // ---------------- NO REPORTS ----------------
            if (filteredReports.isEmpty()) {
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
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF9F9F9)
                            ),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Thumbnail section
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFE0E0E0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val thumbnail = report.images.firstOrNull()

                                    if (thumbnail != null) {
                                        Image(
                                            painter = rememberAsyncImagePainter(thumbnail),
                                            contentDescription = "",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text("No Image", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Report Details
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        report.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF333333)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Category: ${report.category}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF666666)
                                    )
                                    Text(
                                        "Date: ${report.date}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF666666)
                                    )
                                }

                                Button(
                                    onClick = { onViewReport(report) },
                                    modifier = Modifier.height(40.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        "View",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
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

data class LabreportData(
    val reportTitle: String = "",
    val reportCategory: String = "",
    val date: String = "",
    val reportID: String = "",
    val imageUrl: String = "",
    val time: String = ""
)

@Composable
fun ReportDetailsRouteHandler(
    reportId: String,
    onBack: () -> Unit
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
        // Show loading animation while fetching
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        ReportDetailsScreen(
            report = report!!,
            onBack = onBack
        )
    }
}