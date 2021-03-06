package ar.edu.itba.hci.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import ar.edu.itba.hci.android.databinding.FragmentFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterFragment : BottomSheetDialogFragment(), RadioGroup.OnCheckedChangeListener {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    // Scope the ViewModel to activity
    private val model: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        binding.radioGroup.setOnCheckedChangeListener(this)

        binding.switchFavorite.setOnClickListener {
            model.toggleFavorite()
        }

        return binding.root
    }

    override fun onCheckedChanged(p0: RadioGroup?, idRadio: Int) {
        when(idRadio) {
            binding.radioDate.id -> model.saveOrder(Ordering.DATE)
            binding.radioDiff.id -> model.saveOrder(Ordering.DIFFICULTY)
            binding.radioScore.id -> model.saveOrder(Ordering.SCORE)
            binding.radioDuration.id -> model.saveOrder(Ordering.DURATION)
        }
    }

}