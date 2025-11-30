package madproject.chandu.labreportlogger.screens


import LabreportData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import madproject.chandu.labreportlogger.UserPrefs
import madproject.chandu.labreportlogger.ui.theme.crimsonRed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(
    modifier: Modifier = Modifier
) {
    val userEmail = UserPrefs.getEmail(LocalContext.current)


    var query by remember { mutableStateOf("") }
    var reportsList by remember { mutableStateOf(listOf<LabreportData>()) }
    var loadReports by remember { mutableStateOf(true) }


    LaunchedEffect(userEmail) {
        getReportDetails(userEmail) { reports ->
            reportsList = reports
            loadReports = false
        }
    }


    val filtered = remember(query, reportsList) {
        if (query.isBlank()) reportsList
        else {
            reportsList.filter { r ->
                r.reportTitle.contains(query, ignoreCase = true)
                        || r.reportCategory.contains(query, ignoreCase = true)
                        || r.date.contains(query, ignoreCase = true)

            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Your Reports",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = crimsonRed,
                        titleContentColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    SearchBar(
                        query = query,
                        onQueryChange = { query = it }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    )
    { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = "No reports found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            } else {
                items(filtered) { report ->
                    ReportCard(report = report, onView = {
//                        onViewClicked(report)
                    })
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) } // bottom padding
        }
    }
}


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
        },
        placeholder = { Text("Search...") },
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.Gray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(54.dp)
    )
}


@Composable
fun ReportCard(report: LabreportData, onView: () -> Unit) {
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    )
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),

        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = report.reportTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Category: ${report.reportCategory}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Date: ${report.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onView,
                modifier = Modifier
                    .height(44.dp)
                    .widthIn(min = 84.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "View", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    MaterialTheme {
        MyReportsScreen()
    }
}



fun getReportDetails(userMail: String, callback: (List<LabreportData>) -> Unit) {

    val emailKey = userMail.replace(".", ",")
    val databaseReference = FirebaseDatabase.getInstance().getReference("Myreports/$emailKey")

    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val reportsList = mutableListOf<LabreportData>()
            for (newsSnapshot in snapshot.children) {
                val reports = newsSnapshot.getValue(LabreportData::class.java)
                reports?.let { reportsList.add(it) }
            }
            callback(reportsList)
        }

        override fun onCancelled(error: DatabaseError) {
            println("Error: ${error.message}")
            callback(emptyList())
        }
    })
}