package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.LocaleStrings
import com.example.ui.components.AmarKhataPrimaryButton
import com.example.ui.components.AmarKhataTextField
import com.example.ui.components.DistrictSelectorField
import com.example.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("ঢাকা") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(true) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header with Back Arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("register_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "পিছনে যান",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = LocaleStrings.REGISTER_TITLE,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LocaleStrings.REGISTER_SUBTITLE,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Error Banner
            AnimatedVisibility(visible = !uiState.errorMessage.isNullOrBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Registration Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. নাম (Full Name)
                    AmarKhataTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            authViewModel.clearMessages()
                        },
                        label = LocaleStrings.NAME_LABEL,
                        placeholder = LocaleStrings.NAME_HINT,
                        leadingIcon = Icons.Filled.Person,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        testTag = "register_name_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. মোবাইল নম্বর (Phone Number)
                    AmarKhataTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            authViewModel.clearMessages()
                        },
                        label = LocaleStrings.PHONE_LABEL,
                        placeholder = LocaleStrings.PHONE_HINT,
                        leadingIcon = Icons.Filled.Phone,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        testTag = "register_phone_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Email
                    AmarKhataTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            authViewModel.clearMessages()
                        },
                        label = LocaleStrings.EMAIL_LABEL,
                        placeholder = LocaleStrings.EMAIL_HINT,
                        leadingIcon = Icons.Filled.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        testTag = "register_email_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. ব্যবসা / দোকানের নাম (Shop/Business Name)
                    AmarKhataTextField(
                        value = shopName,
                        onValueChange = {
                            shopName = it
                            authViewModel.clearMessages()
                        },
                        label = LocaleStrings.SHOP_NAME_LABEL,
                        placeholder = LocaleStrings.SHOP_NAME_HINT,
                        leadingIcon = Icons.Filled.Storefront,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        testTag = "register_shop_name_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. জেলা (District Picker)
                    DistrictSelectorField(
                        selectedDistrict = district,
                        onDistrictSelected = { district = it },
                        label = LocaleStrings.DISTRICT_LABEL,
                        placeholder = LocaleStrings.DISTRICT_HINT
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 6. পাসওয়ার্ড (Password)
                    AmarKhataTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            authViewModel.clearMessages()
                        },
                        label = LocaleStrings.PASSWORD_LABEL,
                        placeholder = LocaleStrings.PASSWORD_HINT,
                        isPassword = true,
                        leadingIcon = Icons.Filled.Lock,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        testTag = "register_password_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7. কনফার্ম পাসওয়ার্ড (Confirm Password)
                    AmarKhataTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            authViewModel.clearMessages()
                        },
                        label = LocaleStrings.CONFIRM_PASSWORD_LABEL,
                        placeholder = LocaleStrings.CONFIRM_PASSWORD_HINT,
                        isPassword = true,
                        leadingIcon = Icons.Filled.Lock,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                authViewModel.register(
                                    name = name,
                                    phone = phone,
                                    email = email,
                                    shopName = shopName,
                                    district = district,
                                    pass = password,
                                    confirmPass = confirmPassword,
                                    termsAccepted = termsAccepted,
                                    onSuccess = onRegisterSuccess
                                )
                            }
                        ),
                        testTag = "register_confirm_password_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Terms Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { termsAccepted = !termsAccepted }
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = LocaleStrings.TERMS_AGREEMENT,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Registration Button
                    AmarKhataPrimaryButton(
                        text = LocaleStrings.REGISTER_BTN,
                        isLoading = uiState.isLoading,
                        onClick = {
                            focusManager.clearFocus()
                            authViewModel.register(
                                name = name,
                                phone = phone,
                                email = email,
                                shopName = shopName,
                                district = district,
                                pass = password,
                                confirmPass = confirmPassword,
                                termsAccepted = termsAccepted,
                                onSuccess = onRegisterSuccess
                            )
                        },
                        testTag = "register_submit_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Already have account link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = LocaleStrings.ALREADY_HAVE_ACCOUNT,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = LocaleStrings.LOGIN_BTN,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { onNavigateBack() }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("navigate_login_from_register")
                )
            }
        }
    }
}
