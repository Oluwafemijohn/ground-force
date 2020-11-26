package com.trapezoidlimited.groundforce.ui.dashboard

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.trapezoidlimited.groundforce.R
import com.trapezoidlimited.groundforce.databinding.FragmentUserProfileBinding
import com.trapezoidlimited.groundforce.utils.*
import java.util.*


class UserProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentUserProfileBinding? = null

    val binding get() = _binding!!
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var date: String
    private val PERMISSION_REQUEST_CODE: Int = 101
    private val REQUEST_IMAGE_CAPTURE = 1

    private var googleAccount: GoogleSignInAccount? = null

    private lateinit var profileImageView: ImageView

    /** onCreateView over ride function **/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        /** setting toolbar text **/
        binding.fragmentUserProfileTb.toolbarTitle.text = getString(R.string.profile_str)
        binding.fragmentUserProfileTb.toolbarTitle.setTextColor(resources.getColor(R.color.colorWhite))

        /** set navigation arrow from drawable **/
        binding.fragmentUserProfileTb.toolbarTransparentFragment.setNavigationIcon(R.drawable.ic_arrow_white_back)

        /** set navigation to go to the previous screen on click of navigation arrow **/
        binding.fragmentUserProfileTb.toolbarTransparentFragment.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /** Array adapter for spinner drop down for sex **/
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sex,
            android.R.layout.simple_spinner_item
        ).also { sexAdapter ->
            // Specify the layout to use when the list of choices appears
            sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.fragmentUserProfileGenderSp.adapter = sexAdapter

        }

        /** Array adapter for spinner drop down for religion **/
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.religion,
            android.R.layout.simple_spinner_item
        ).also { religionAdapter ->
            // Specify the layout to use when the list of choices appears
            religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner

            binding.fragmentUserProfileReligiousSp.adapter = religionAdapter
        }

        /** listener for sex option **/
        binding.fragmentUserProfileGenderSp.onItemSelectedListener = this

        return binding.root
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.getItemAtPosition(position)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    /** onActivityCreated **/
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profileImageView = binding.fragmentCreateProfileOneProfileImageIv

        validateFields()

        val dateButton = binding.fragmentUserProfileDateBirthEt

        /** Show the date button on click of date button **/
        dateButton.setOnClickListener {
            showDatePickerDialog(requireView())
        }

        /** Date set listener **/
        dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            date = "${month + 1}/$day/$year"
            dateButton.setText(date)
        }

        //Open Camera and capture Image
        profileImageView.setOnClickListener {
            if (checkPermission()) dispatchTakePictureIntent() else requestPermission()
        }
    }

    private fun validateFields() {
        val fields: MutableList<JDataClass> = mutableListOf(
            JDataClass(
                editText = binding.fragmentUserProfileFirstNameEt,
                editTextInputLayout = binding.fragmentUserProfileFirstNameTil,
                errorMessage = JDErrorConstants.NAME_FIELD_ERROR,
                validator = { it.jdValidateName(it.text.toString()) }
            ),
            JDataClass(
                editText = binding.fragmentUserProfileLastNameEt,
                editTextInputLayout = binding.fragmentUserProfileLastNameTil,
                errorMessage = JDErrorConstants.NAME_FIELD_ERROR,
                validator = { it.jdValidateName(it.text.toString()) }
            ),
            JDataClass(
                editText = binding.fragmentUserProfileDateBirthEt,
                editTextInputLayout = binding.fragmentUserProfileDateBirthTil,
                errorMessage = JDErrorConstants.NAME_FIELD_ERROR,
                validator = { it.jdValidateName(it.text.toString()) }
            ),
            JDataClass(
                editText = binding.fragmentUserProfileEmailAddressEt,
                editTextInputLayout = binding.fragmentUserProfileEmailAddressTil,
                errorMessage = JDErrorConstants.INVALID_EMAIL_ERROR,
                validator = { it.jdValidateEmail(it.text.toString()) }
            ),
            JDataClass(
                editText = binding.fragmentUserProfileAdditionalNumberEt,
                editTextInputLayout = binding.fragmentUserProfileAdditionalNumberTil,
                errorMessage = JDErrorConstants.INVALID_PHONE_NUMBER_ERROR,
                validator = { it.jdValidateAdditionalPhone(it.text.toString()) }
            ),
            JDataClass(
                editText = binding.fragmentUserProfileResidentialAddressEt,
                editTextInputLayout = binding.fragmentUserProfileResidentialAddressTil,
                errorMessage = JDErrorConstants.NAME_FIELD_ERROR,
                validator = { it.jdValidateName(it.text.toString()) }
            ),
            JDataClass(
                editText = binding.fragmentUserProfileAccountNumberEt,
                editTextInputLayout = binding.fragmentUserProfileAccountNumberTil,
                errorMessage = JDErrorConstants.BANK_ACCOUNT_NUMBER_ERROR,
                validator = { it.jdValidateAccountNumber(it.text.toString()) }
            )
        )

        JDFormValidator.Builder()
            .addFieldsToValidate(fields)
            .removeErrorIcon(true)
            .watchWhileTyping(true)
            .build()
    }


    /** Take picture function **/
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            e.message?.let { showSnackBar(requireView(), it) }
        }
    }

    /** onActivityResult function place the captured image on the image view place holder **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            profileImageView.setImageBitmap(imageBitmap)
        }
    }


    /** Check for user permission to access phone camera **/
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(), CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(), READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    /** requestPermission for user permission to access phone camera **/
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }


    /** On request permission result grant user permission or show a permission denied message **/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }


    /** Show Date picker Dialog Function **/
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDatePickerDialog(v: View) {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        /** Date dialog picker style **/
        val dialog = DatePickerDialog(
            requireContext(),
            android.R.style.ThemeOverlay_Material_Dialog_Alert,
            dateSetListener, year, month, day
        )
        dialog.show()

    }


    override fun onStart() {
        super.onStart()
        googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
    }
}