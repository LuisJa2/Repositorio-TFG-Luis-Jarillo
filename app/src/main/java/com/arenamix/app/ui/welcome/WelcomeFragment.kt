package com.arenamix.app.ui.welcome

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arenamix.app.R
import com.arenamix.app.data.ApiService
import com.arenamix.app.data.LoginRequest
import com.arenamix.app.data.RegisterRequest
import com.arenamix.app.data.RetrofitClient
import com.arenamix.app.data.SessionManager
import com.arenamix.app.databinding.FragmentWelcomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si ya hay sesión activa, saltar directo al home
        if (SessionManager.isLoggedIn) {
            navigateToHome()
            return
        }

        binding.btnExplore.setOnClickListener { navigateToHome() }
        binding.tvGuest.setOnClickListener   { navigateToHome() }
        binding.btnLogin.setOnClickListener    { showLoginDialog() }
        binding.btnRegister.setOnClickListener { showRegisterDialog() }

        // Selección de deporte (solo visual)
        val sports = listOf(binding.sportFutbol, binding.sportPadel, binding.sportBasket)
        sports.forEach { card ->
            card.setOnClickListener {
                sports.forEach { it.setBackgroundResource(R.drawable.bg_sport_card) }
                card.setBackgroundResource(R.drawable.bg_sport_card_selected)
            }
        }
    }

    // ── Diálogo de Login ──────────────────────────────────────────────────────

    private fun showLoginDialog() {
        val layout = buildDialogLayout()
        val emailInput = editText("Email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val passwordInput = editText("Contraseña",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        layout.addView(emailInput)
        layout.addView(passwordInput)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Iniciar sesión")
            .setView(layout)
            .setPositiveButton("Entrar") { _, _ ->
                val email    = emailInput.text.toString().trim()
                val password = passwordInput.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    toast("Rellena todos los campos")
                } else {
                    performLogin(email, password)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ── Diálogo de Registro ───────────────────────────────────────────────────

    private fun showRegisterDialog() {
        val layout = buildDialogLayout()
        val usernameInput = editText("Nombre de usuario", InputType.TYPE_CLASS_TEXT)
        val emailInput    = editText("Email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val passwordInput = editText("Contraseña",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        layout.addView(usernameInput)
        layout.addView(emailInput)
        layout.addView(passwordInput)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Crear cuenta")
            .setView(layout)
            .setPositiveButton("Registrarse") { _, _ ->
                val username = usernameInput.text.toString().trim()
                val email    = emailInput.text.toString().trim()
                val password = passwordInput.text.toString()
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    toast("Rellena todos los campos")
                } else {
                    performRegister(username, email, password)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ── Llamadas a la API ─────────────────────────────────────────────────────

    private fun performLogin(email: String, password: String) {
        toast("Conectando con el servidor…")
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = RetrofitClient.api.login(LoginRequest(email, password))
                SessionManager.token    = res.token
                SessionManager.username = res.username
                toast("¡Bienvenido, ${res.username}!")
                navigateToHome()
            } catch (e: HttpException) {
                when (e.code()) {
                    401  -> toast("Email o contraseña incorrectos")
                    else -> toast("Error del servidor (${e.code()})")
                }
            } catch (e: Exception) {
                toast("No se pudo conectar. El servidor puede estar arrancando, espera unos segundos y vuelve a intentarlo.")
            }
        }
    }

    private fun performRegister(username: String, email: String, password: String) {
        toast("Conectando con el servidor…")
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = RetrofitClient.api.registro(RegisterRequest(username, email, password))
                SessionManager.token    = res.token
                SessionManager.username = res.username
                toast("¡Cuenta creada! Bienvenido, ${res.username}!")
                navigateToHome()
            } catch (e: HttpException) {
                when (e.code()) {
                    409  -> toast("El email o nombre de usuario ya existe")
                    else -> toast("Error del servidor (${e.code()})")
                }
            } catch (e: Exception) {
                toast("No se pudo conectar. El servidor puede estar arrancando, espera unos segundos y vuelve a intentarlo.")
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildDialogLayout() = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(64, 24, 64, 0)
    }

    private fun editText(hint: String, inputType: Int) = EditText(requireContext()).apply {
        this.hint = hint
        this.inputType = inputType
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_welcome_to_home)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
