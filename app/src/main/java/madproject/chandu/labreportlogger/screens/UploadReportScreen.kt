import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadReportScreen() {
    var reportDescription by remember { mutableStateOf("") }
    var serviceDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_DATE)) }
    var selectedReportType by remember { mutableStateOf("Blood Test") }
    var isTypeDropdownExpanded by remember { mutableStateOf(false) }
    var uploadedFileName by remember { mutableStateOf("No file selected") }

    val reportTypeOptions = listOf(
        "Blood Test", "X-Ray / Imaging", "Prescription", "Vaccination Record",
        "ECG / Cardiac", "Discharge Summary", "Other Documents"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Upload Medical Report", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Report Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Report Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 1. Report Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = isTypeDropdownExpanded,
                        onExpandedChange = { isTypeDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = selectedReportType,
                            onValueChange = { },
                            label = { Text("Report Type") },
                            leadingIcon = { Icon(Icons.Default.Menu, contentDescription = "Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isTypeDropdownExpanded,
                            onDismissRequest = { isTypeDropdownExpanded = false }
                        ) {
                            reportTypeOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedReportType = selectionOption
                                        isTypeDropdownExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    // 2. Report Description Input
                    OutlinedTextField(
                        value = reportDescription,
                        onValueChange = { reportDescription = it },
                        label = { Text("Description / Doctor") },
                        placeholder = { Text("e.g., Annual Check-up, Dr. Smith") },
                        leadingIcon = { Icon(Icons.Default.Menu, contentDescription = "Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 3. Service Date Input
                    OutlinedTextField(
                        value = serviceDate,
                        onValueChange = { serviceDate = it },
                        label = { Text("Date of Service") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Date") },
                        trailingIcon = {
                            IconButton(onClick = { /* TODO: Launch Date Picker Dialog */ }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Select Date"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // File Upload Section Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Document Upload",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Button to trigger file selection
                        Button(onClick = {
                            // TODO: Implement file picker intent/logic
                            uploadedFileName = "${selectedReportType.replace(" ", "_").lowercase()}_${System.currentTimeMillis()}.pdf"
                        }) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Upload Document")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Document")
                        }

                        // Display selected file name
                        Text(
                            text = uploadedFileName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (uploadedFileName == "No file selected") {
                                MaterialTheme.colorScheme.error // Use error color to draw attention if no file is selected
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.weight(1f).padding(start = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Upload Submission Button
            Button(
                onClick = {
                    println("Attempting to upload report: $selectedReportType - $reportDescription")
                },
                enabled = reportDescription.isNotBlank() && uploadedFileName != "No file selected",
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Save Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UploadMedicalReportScreenPreview() {
    MaterialTheme {
        UploadReportScreen()
    }
}