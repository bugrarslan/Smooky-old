package com.bugra.smooky.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.bugra.smooky.R
import com.bugra.smooky.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener { signInClicked(view) }
        binding.signUpButton.setOnClickListener { signUpClicked(view) }

        auth = Firebase.auth
    }

    fun signInClicked(view: View) {
        val username = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (username.equals("") || password.equals("")) {
            Toast.makeText(requireActivity(), "Kullanıcı adınızı ve şifrenizi giriniz!", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(username, password).addOnSuccessListener {
                val intent = Intent(requireActivity(), PostActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }.addOnFailureListener {
                Toast.makeText(requireActivity(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signUpClicked(view: View) {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(action)
    }
}