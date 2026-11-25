package com.comp3074.foodquest.domain

import android.util.Patterns
import com.comp3074.foodquest.data.TeamMember

object MemberValidation {
    data class Errors(val name: String? = null, val email: String? = null, val phone: String? = null) {
        val valid get() = name == null && email == null && phone == null
    }

    fun clean(member: TeamMember) = member.copy(name = member.name.trim(), studentId = member.studentId.trim(),
        email = member.email.trim(), phone = member.phone.trim())

    fun errors(member: TeamMember): Errors {
        val value = clean(member)
        return Errors(
            name = if (value.name.isBlank()) "Enter a name" else null,
            email = if (value.email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(value.email).matches()) "Enter a valid email" else null,
            phone = if (value.phone.isNotEmpty() && !Patterns.PHONE.matcher(value.phone).matches()) "Enter a valid phone number" else null
        )
    }
}
