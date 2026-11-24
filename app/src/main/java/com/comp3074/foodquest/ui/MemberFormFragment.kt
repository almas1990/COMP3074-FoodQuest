package com.comp3074.foodquest.ui

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import com.comp3074.foodquest.R
import com.comp3074.foodquest.data.TeamMember
import com.comp3074.foodquest.domain.MemberValidation

class MemberFormFragment : ScreenFragment() {
    private val fields = mutableMapOf<String, EditText>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong("memberId") ?: 0L
        back(body)
        heading(body, if (id == 0L) "Add member" else "Edit member")
        val loading = label(body, "Loading member...")
        var rendered = false
        var saveButton: android.widget.Button? = null
        // A new activity may still be loading Room; an empty initial LiveData is
        // not proof that an existing member has been deleted.
        model.busy.observe(viewLifecycleOwner) { busy ->
            saveButton?.isEnabled = !busy
            if (busy || rendered) return@observe
            rendered = true
            loading.visibility = View.GONE
            val member = model.members.value.orEmpty().find { it.id == id }
            if (id != 0L && member == null) {
                label(body, "This team member is no longer available. Return to the team list.")
                return@observe
            }
            fun input(key: String, title: String, initial: String, resourceId: Int,
                      type: Int = InputType.TYPE_CLASS_TEXT): EditText {
                val value = savedInstanceState?.getString("draft_$key") ?: initial
                return field(body, title, value, resourceId, type).also { field ->
                    fields[key] = field
                    field.doAfterTextChanged { field.error = null }
                }
            }
            val name = input("name", "Name", member?.name.orEmpty(), R.id.member_name)
            val studentId = input("studentId", "Student ID", member?.studentId.orEmpty(), R.id.member_student_id, InputType.TYPE_CLASS_NUMBER)
            val email = input("email", "Email", member?.email.orEmpty(), R.id.member_email,
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            val phone = input("phone", "Phone", member?.phone.orEmpty(), R.id.member_phone, InputType.TYPE_CLASS_PHONE)
            saveButton = action(body, "Save member") {
                val value = MemberValidation.clean(TeamMember(id, name.text.toString(), studentId.text.toString(),
                    email.text.toString(), phone.text.toString()))
                val errors = MemberValidation.errors(value)
                name.error = errors.name
                email.error = errors.email
                phone.error = errors.phone
                if (!errors.valid) {
                    listOf(name, email, phone).firstOrNull { it.error != null }?.requestFocus()
                } else {
                    model.saveMember(value) { if (isAdded) parentFragmentManager.popBackStack() }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        fields.forEach { (key, field) -> outState.putString("draft_$key", field.text.toString()) }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        fields.clear()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(id: Long) = MemberFormFragment().apply { arguments = bundleOf("memberId" to id) }
    }
}
