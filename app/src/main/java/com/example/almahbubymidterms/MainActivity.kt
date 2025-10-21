package com.example.almahbubymidterms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.almahbubymidterms.ui.theme.AlmahbubymidtermsTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlmahbubymidtermsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Dash: Screen("Dash")
    data object Input: Screen("Input")
    data object Edit: Screen("Edit")
}

@Composable
fun Main(modifier: Modifier) {
    val navController = rememberNavController()
    val contactsState = rememberSaveable { mutableStateListOf<Contact>() }
    val snackbarHostState = remember { SnackbarHostState() }

    fun onAddContact(name: String, address: String, phone: String, email: String): Unit {

        if (address.split(" ").size >= 5) {
            contactsState.add(Contact(name, address, phone, email))
            navController.popBackStack()
        }
    }

    fun onEditContact(index: Int, contact: Contact) {
        if (contact.address.split(" ").size >= 5) {
            contactsState[index] = contact
            navController.popBackStack()
        }
    }

    NavHost(navController = navController, startDestination = Screen.Dash.route) {
        composable(route = Screen.Dash.route) {
            ListContact(modifier, navController = navController, contacts = contactsState)
        }
        composable(route = Screen.Input.route) {
            InputContacts(modifier, onNavigateBack = {navController.popBackStack()},
                onAddContact = { name, address, phone, email ->
                    onAddContact(name, address, phone, email)
                },
                onEditContact = { index, contact ->
                    onEditContact(index, contact)
                })
        }
    }
}

@Composable
fun ContactItem(name: String, address: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 12.dp)) {
        Text(name, fontWeight = FontWeight.Bold)
        Text(address)
    }
}

enum class InputMode {
    ADD,
    EDIT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputContacts(modifier: Modifier = Modifier, onNavigateBack: () -> Unit,
                  onAddContact: (name: String, address: String, phone: String,
                          email: String) -> Unit,
                  onEditContact: (index: Int, contact: Contact) -> Unit,
mode: InputMode = InputMode.ADD, index: Int = 0, contact: Contact = Contact()
) {
    var inputName by rememberSaveable { mutableStateOf(contact.name) }
    var inputAddress by rememberSaveable { mutableStateOf(contact.address) }
    var inputPhone by rememberSaveable { mutableStateOf(contact.phone) }
    var inputEmail by rememberSaveable { mutableStateOf(contact.email) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title={
            Text("Add a contact")
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        })
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputName,
                onValueChange = {inputName = it},
                label = {Text("Name")},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inputAddress,
                onValueChange = {inputAddress = it},
                label = {Text("Address")},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inputEmail,
                onValueChange = {inputEmail = it},
                label = {Text("Email")},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inputPhone,
                onValueChange = {inputPhone = it},
                label = {Text("Phone No.")},
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                if (mode == InputMode.EDIT) {
                    onEditContact(
                        index,
                        Contact(inputName, inputAddress, inputPhone, inputEmail)
                    )
                }
                else {
                    onAddContact(
                        inputName, inputAddress,
                        inputPhone, inputEmail
                    )
                }

            }) {
                Text(if (mode == InputMode.EDIT) "Edit" else "Add")
            }
        }
    }
}

class Contact(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContact(modifier: Modifier, navController: NavHostController, contacts: SnapshotStateList<Contact>) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
            Text("Dashboard")
        })
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn() {
                items(contacts) {contact ->
                    ContactItem(contact.name, contact.address)
                }
            }
            FloatingActionButton(
                onClick = {navController.navigate(Screen.Input.route)},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-32).dp, y = (-32).dp)
            ) {
                Icon(Icons.Rounded.Add, "Add contact.")
            }
        }
    }
}