package com.comp3074.foodquest.ui

import com.comp3074.foodquest.R

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import androidx.core.os.bundleOf
import com.comp3074.foodquest.data.TeamMember

class MemberFormFragment : ScreenFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong("memberId") ?: 0L
        val member = model.members.value.orEmpty().find { it.id == id }
        back(body)
        heading(body, if (id == 0L) "Add member" else "Edit member")
        val name = field(body, "Name", member?.name.orEmpty(), R.id.member_name)
        val studentId = field(body, "Student ID", member?.studentId.orEmpty(), R.id.member_student_id, InputType.TYPE_CLASS_NUMBER)
        val email = field(body, "Email", member?.email.orEmpty(), R.id.member_email, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val phone = field(body, "Phone", member?.phone.orEmpty(), R.id.member_phone, InputType.TYPE_CLASS_PHONE)
        val save = action(body, "Save member") {
            var valid = true
            if (name.text.isBlank()) { name.error = "Required"; valid = false }
            if (email.text.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
                email.error = "Enter a valid email"; valid = false
            }
            if (phone.text.isNotBlank() && !Patterns.PHONE.matcher(phone.text).matches()) {
                phone.error = "Enter a valid phone number"; valid = false
            }
            if (valid) model.saveMember(TeamMember(id, name.text.toString(), studentId.text.toString(),
                email.text.toString(), phone.text.toString())) { if (isAdded) parentFragmentManager.popBackStack() }
        }
        model.busy.observe(viewLifecycleOwner) { save.isEnabled = !it }
    }

    companion object {
        fun newInstance(id: Long) = MemberFormFragment().apply { arguments = bundleOf("memberId" to id) }
    }
}
