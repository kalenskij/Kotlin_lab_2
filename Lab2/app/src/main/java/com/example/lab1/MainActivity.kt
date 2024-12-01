package com.example.lab1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1.ui.theme.Lab1Theme

class First : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab1Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EmissionCalculatorApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EmissionCalculatorApp(modifier: Modifier = Modifier) {
    // State for input values
    var coal by remember { mutableStateOf("") }
    var fuelOil by remember { mutableStateOf("") }
    var naturalGas by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    // Helper function to calculate emissions
    fun calculateEmissions() {
        val coalAmount = coal.toDoubleOrNull() ?: 0.0
        val fuelOilAmount = fuelOil.toDoubleOrNull() ?: 0.0
        val gasAmount = naturalGas.toDoubleOrNull() ?: 0.0

        // Constants for emission factors and heat values
        val emissionFactors = EmissionFactors()
        val heatValues = HeatValues()

        val filterEfficiency = 0.985

        Log.d("EmissionCalculator", "Coal Amount: $coalAmount, Fuel Oil Amount: $fuelOilAmount, Natural Gas Amount: $gasAmount")

        // Calculate emissions for coal
        val totalCoalEmissions = calculateEmissionsForFuel(emissionFactors.coal, heatValues.coal, coalAmount, filterEfficiency)
        val totalFuelOilEmissions = calculateEmissionsForFuel(emissionFactors.fuelOil, heatValues.fuelOil, fuelOilAmount, filterEfficiency)
        val totalGasEmissions = calculateEmissionsForFuel(emissionFactors.gas, heatValues.gas, gasAmount, filterEfficiency)

        // Total emissions
        val totalEmissions = totalCoalEmissions + totalFuelOilEmissions + totalGasEmissions
        Log.d("EmissionCalculator", "Total Emissions: $totalEmissions")

        // Format the result
        val totalCoalEmissions_f = totalCoalEmissions * (1 - filterEfficiency)
        val totalFuelOilEmissions_f = totalFuelOilEmissions * (1 - filterEfficiency)
        val totalGasEmissions_f = totalGasEmissions * (1 - filterEfficiency)
        val totalEmissions_f = totalEmissions * (1 - filterEfficiency)
        result = formatResult(totalCoalEmissions, totalFuelOilEmissions, totalGasEmissions, totalEmissions, totalCoalEmissions_f, totalFuelOilEmissions_f, totalGasEmissions_f, totalEmissions_f)
    }

    // Column layout for the UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InputField(label = "Вугілля (в тоннах)", value = coal, onValueChange = { coal = it })
        InputField(label = "Мазут (в тоннах)", value = fuelOil, onValueChange = { fuelOil = it })
        InputField(label = "Природний газ (в м³)", value = naturalGas, onValueChange = { naturalGas = it })

        // Calculate button
        Button(onClick = { calculateEmissions() }, modifier = Modifier.fillMaxWidth()) {
            Text("Розрахувати")
        }

        // Display result
        Text(text = result, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

// Data classes for emission factors and heat values
data class EmissionFactors(val coal: Double = 150.0, val fuelOil: Double = 0.57, val gas: Double = 0.0)
data class HeatValues(val coal: Double = 20.47, val fuelOil: Double = 40.40, val gas: Double = 33.08)

// Function to calculate emissions for a specific fuel
fun calculateEmissionsForFuel(emissionFactor: Double, heatValue: Double, amount: Double, filterEfficiency: Double): Double {
    val emissions = (emissionFactor * heatValue * amount) / 1_000_000
    return emissions
}

// Function to format the result as a string
fun formatResult(coal: Double, fuelOil: Double, gas: Double, total: Double,coal_f: Double, fuelOil_f: Double, gas_f: Double, total_f: Double): String {
    return """
        Валові викиди при спалюванні палива без фільтра:
        Вугілля: %.4f т
        Мазут: %.4f т
        Природний газ: %.4f т
        Загальна кількість викидів: %.4f т
        
        
        Валові викиди при спалюванні палива з фільтром:
        Вугілля: %.4f т
        Мазут: %.4f т
        Природний газ: %.4f т
        Загальна кількість викидів: %.4f т
    """.trimIndent().format(coal, fuelOil, gas, total, coal_f, fuelOil_f, gas_f, total_f)
}

@Preview(showBackground = true)
@Composable
fun EmissionCalculatorPreview() {
    Lab1Theme {
        EmissionCalculatorApp()
    }
}