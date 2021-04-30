package org.d3if2081.hitungbmi.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import org.d3if2081.hitungbmi.R
import org.d3if2081.hitungbmi.data.KategoriBmi
import org.d3if2081.hitungbmi.databinding.FragmentHitungBinding

class HitungFragment : Fragment() {
    private val viewModel: HitungViewModel by viewModels()
    private lateinit var binding: FragmentHitungBinding
    private lateinit var kategoriBmi: KategoriBmi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHitungBinding.inflate(layoutInflater, container, false)

        setHasOptionsMenu(true)

        binding.button.setOnClickListener { hitungBmi() }

        binding.shareButton.setOnClickListener { shareData() }

        binding.saranButton.setOnClickListener { view : View ->
            view.findNavController().navigate(
                HitungFragmentDirections.actionHitungFragmentToSaranFragment(kategoriBmi)
            )
        }

        binding.buttonReset.setOnClickListener { resetAll() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getHasilBmi().observe(viewLifecycleOwner, {
            if (it == null) return@observe
            binding.bmiTextView.text = getString(R.string.bmi_x, it.bmi)
            binding.kategoriTextView.text = getString(R.string.kategori_x, getKategori(it.kategori))
            binding.buttonGroup.visibility = View.VISIBLE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_about) {
            findNavController().navigate(
                R.id.action_hitungFragment_to_aboutFragment
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Melakukan reset terhadap field text berat, tinggi, dan text bmi juga kategori bmi
    private fun resetAll() {
        val emptyString = ""
        binding.beratEditText.text = null
        binding.tinggiEditText.text = null

        binding.bmiTextView.text = null
        binding.kategoriTextView.text = null

    }

    // Melakukan perhitungan terhadap berat, tinggi, dan jenis kelamin dari input user
    private fun hitungBmi() {
        val berat = binding.beratEditText.text.toString()
        if (TextUtils.isEmpty(berat)) {
            Toast.makeText(context, R.string.berat_invalid, Toast.LENGTH_LONG).show()
            return
        }

        val tinggi = binding.tinggiEditText.text.toString()
        if (TextUtils.isEmpty(tinggi)) {
            Toast.makeText(context, R.string.tinggi_invalid, Toast.LENGTH_LONG).show()
            return
        }

        val selectedId = binding.radioGroup.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(context, R.string.gender_invalid, Toast.LENGTH_LONG).show()
            return
        }

        val isMale = selectedId == R.id.priaRadioButton

        viewModel.hitungBmi(berat, tinggi, isMale)
    }

    // Mendapatkan informasi tipe bmi
    private fun getKategori(kategori: KategoriBmi): String {
        val stringRes = when(kategori) {
            KategoriBmi.KURUS -> R.string.kurus
            KategoriBmi.IDEAL -> R.string.ideal
            KategoriBmi.GEMUK -> R.string.gemuk
        }

        return getString(stringRes)
    }

    private fun shareData() {
        val selectedId = binding.radioGroup.checkedRadioButtonId
        val gender = if (selectedId == R.id.priaRadioButton)
            getString(R.string.pria)
        else
            getString(R.string.wanita)

        val message = getString(R.string.bagikan_template,
                binding.beratEditText.text,
                binding.tinggiEditText.text,
                gender,
                binding.bmiTextView.text,
                binding.kategoriTextView.text)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain").putExtra(Intent.EXTRA_TEXT, message)
        if (shareIntent.resolveActivity(
                requireActivity().packageManager) != null) {
            startActivity(shareIntent)
        }
    }
}