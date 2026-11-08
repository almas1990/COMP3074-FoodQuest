package com.comp3074.foodquest.ui

import android.os.Bundle
import android.view.View

class TeamFragment : ScreenFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model.members.observe(viewLifecycleOwner) { members ->
            body.removeAllViews()
            heading(body, "Team P23")
            action(body, "Add member") { host.open(MemberFormFragment()) }
            members.forEach { member ->
                section(body).also { row ->
                    heading(row, member.name)
                    label(row, member.studentId)
                    if (member.email.isNotBlank()) {
                        label(row, member.email)
                        action(row, "Email") { ExternalActions.email(requireContext(), member.email, "Food Quest", "") }
                    }
                    if (member.phone.isNotBlank()) {
                        label(row, member.phone)
                        action(row, "Text message") { ExternalActions.sms(requireContext(), member.phone) }
                        action(row, "Call") { ExternalActions.phone(requireContext(), member.phone) }
                    }
                    action(row, "Edit member") { host.open(MemberFormFragment.newInstance(member.id)) }
                }
            }
        }
    }
}
