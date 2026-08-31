package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    initialTab: Int = 0,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) } // 0: Terms of Service, 1: Privacy Policy
    val scrollState = rememberScrollState()

    // Reset scroll when switching tabs
    LaunchedEffect(selectedTab) {
        scrollState.scrollTo(0)
    }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Privacy & Security",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF0F172A)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )

                    // Tab Selector
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = Primary,
                        divider = { HorizontalDivider(color = Color(0xFFE2E8F0)) }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    "Terms of Service",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            icon = {
                                Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Text(
                                    "Privacy Policy",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            icon = {
                                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == 0) {
                TermsOfServiceContent()
            } else {
                PrivacyPolicyContent()
            }
        }
    }
}

@Composable
private fun TermsOfServiceContent() {
    // Header Banner
    Surface(
        color = Color(0xFFEFF6FF),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Terms of Service — TrueLine",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Last updated: 7 August 2026  ·  Effective: 7 August 2026",
                fontSize = 12.5.sp,
                color = Color(0xFF3B82F6),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Published by Nexvert Technologies in accordance with Rule 3(1) of the Information Technology (Intermediary Guidelines and Digital Media Ethics Code) Rules, 2021.",
                fontSize = 13.sp,
                color = Color(0xFF1E293B),
                lineHeight = 18.sp
            )
        }
    }

    // Short Summary Box
    LegalCalloutBox(
        title = "In Short",
        content = "Coins are prepaid credit for using TrueLine. They are not money, cannot be withdrawn or transferred, and have no value outside the Platform. There is no autopay and no subscription."
    )

    // Sections
    LegalSectionCard(
        number = "1",
        title = "About these Terms",
        body = """These Terms of Service ("Terms") govern your access to and use of the TrueLine mobile application and the website at truelineapp.in (together, the "Platform"), operated by Nexvert Technologies ("TrueLine", "we", "us", "our"), a company registered in India with its registered office at 180/2, HSR Layout, Bengaluru, Karnataka 560068.

These Terms are published in accordance with Rule 3(1) of the Information Technology (Intermediary Guidelines and Digital Media Ethics Code) Rules, 2021, read with the Information Technology Act, 2000, and should be read together with our Privacy Policy.

By creating an account or using the Platform, you confirm that you have read, understood and agree to these Terms. If you do not agree, please do not use the Platform."""
    )

    LegalSectionCard(
        number = "2",
        title = "Eligibility — 18+ only",
        body = """TrueLine is strictly for adults. You may use the Platform only if you are 18 years of age or older. We verify age against the date of birth on the government-issued identity document submitted during Listener verification, and we require confirmation of adult status from users.

If we become aware that a person under 18 is using the Platform, we will suspend and remove that account and delete the associated personal data."""
    )

    LegalSectionCard(
        number = "3",
        title = "Our role as an intermediary",
        body = """TrueLine is an intermediary as defined under the Information Technology Act, 2000. We provide a platform that connects users with independent Listeners for one-to-one voice conversations. We do not participate in, script, direct or control those conversations.

We do not endorse any Listener or any statement made by a Listener. Views expressed during a call are those of the individuals on that call."""
    )

    LegalSectionCard(
        number = "4",
        title = "Your account",
        bullets = listOf(
            "You register with your mobile number and a one-time password (OTP). You are responsible for keeping access to that number secure.",
            "One account per person. Creating multiple accounts to abuse promotions, bonuses or referrals is prohibited.",
            "You are responsible for all activity on your account.",
            "Tell us immediately at support@truelineapp.in if you believe your account has been accessed without your permission."
        )
    )

    LegalSectionCard(
        number = "5",
        title = "Coins, pricing and payments",
        body = """5.1 What coins are:
Coins are a prepaid, non-monetary in-app credit used solely to access services on the Platform. Coins:
• Are not a currency, deposit, e-money or prepaid payment instrument;
• Cannot be encashed, withdrawn, redeemed for money, or transferred to another user or account;
• Carry no interest and no ownership rights;
• Can be used only for services offered on the Platform.

5.2 Rate:
Voice calls are charged at 9 coins per minute. Any change to this rate will be notified in the app before it takes effect and will not apply retrospectively to coins already purchased.

5.3 No autopay:
TrueLine does not operate autopay, auto-debit, auto-renewal, standing instructions or subscriptions. Coins are only ever purchased when you actively choose to recharge and complete payment.

5.4 Billing:
• Coins are deducted for connected call time, metered as described in the app.
• Your coin balance and estimated remaining talk time are visible before and during a call.
• Payments are processed by third-party payment providers. We do not store your card, UPI credentials or banking details.
• Applicable taxes, including GST, are included in the displayed price of recharge packs.

5.5 Call recording:
Calls made on TrueLine are recorded. You and the Listener are both notified of this at the start of every call, before the conversation begins. By proceeding with a call, you consent to that recording. Recordings are used only to investigate reports of misconduct and to resolve disputes. They are encrypted, access-controlled, retained for a limited period (30 days), and are never shared with other users or Listeners.

5.6 Bonus coins:
We may offer bonus or promotional coins. Bonus coins are promotional, carry no monetary value, and may be withdrawn or adjusted where we reasonably believe they have been obtained through abuse, fraud or multiple accounts."""
    )

    LegalSectionCard(
        number = "6",
        title = "Refunds",
        body = """Coins are generally non-refundable once purchased, as they provide immediate access to services.

We will, however, credit coins back to your balance where:
• A call fails or drops due to a fault on the Platform;
• You were charged for a call that did not connect;
• A technical error resulted in an incorrect deduction.

Raise any billing concern within 7 days at support@truelineapp.in with the transaction details. We will investigate and respond within 7 working days."""
    )

    LegalSectionCard(
        number = "7",
        title = "Listeners",
        body = """Listeners on TrueLine are independent service providers. They are not employees, agents or partners of Nexvert Technologies, and no employment relationship arises between a Listener and TrueLine.

Every Listener is a real person who has completed identity verification, including a liveness check and government identity document check, before being permitted to take calls. TrueLine does not use artificial voices, bots or AI personas in place of Listeners.

Listeners set their own availability. We do not guarantee that any particular Listener will be available at any given time."""
    )

    LegalSectionCard(
        number = "8 & 9",
        title = "Acceptable use & Prohibited conduct",
        body = "TrueLine is a space for respectful conversation. You must not, and must not attempt to:",
        bullets = listOf(
            "Use the Platform if you are under 18;",
            "Engage in sexually explicit, obscene, abusive, threatening, harassing or defamatory conduct;",
            "Solicit or attempt to arrange sexual services, or any illegal service;",
            "Request, share or attempt to obtain personal contact details (phone number, social media handles, address) from a Listener, or attempt to move the conversation off-Platform;",
            "Make your own recording of a call, or reproduce, publish or distribute any call or part of a call, or any TrueLine call recording;",
            "Impersonate another person, or misrepresent your age or identity;",
            "Use the Platform to promote, advertise or solicit any business;",
            "Upload or transmit content that is unlawful, infringing, hateful, or harmful to minors;",
            "Use bots, scripts, automated tools, or attempt to reverse-engineer, disrupt or gain unauthorised access to the Platform;",
            "Abuse promotions, bonuses or the referral programme, including through multiple accounts;",
            "Do anything that violates applicable Indian law, including the Information Technology Act, 2000 and rules made under it."
        )
    )

    LegalSectionCard(
        number = "10",
        title = "Reporting and enforcement",
        body = """You can report a user, a Listener or a conversation from within the app. We aim to acknowledge reports within 24 hours and to act on them promptly.

Where we find a breach of these Terms, we may issue a warning, restrict features, withhold or reverse bonus coins, suspend the account, or terminate it — depending on the seriousness of the breach and any repetition."""
    )

    LegalSectionCard(
        number = "11 to 13",
        title = "IP, Termination & Account Deletion",
        body = """11. Content and intellectual property:
The TrueLine name, logo, design, software and all Platform content are owned by Nexvert Technologies and protected by applicable intellectual property law.

12. Suspension and termination:
We may suspend or terminate your access, with or without notice, where we reasonably believe you have breached these Terms or where required by law.

13. Deleting your account:
You may delete your account at any time from within the app. You do not need to contact us or obtain approval. Unused coins are not refundable in money on account deletion."""
    )

    LegalCalloutBox(
        title = "14. What TrueLine is not",
        content = "TrueLine offers conversation and companionship. It is not a medical, psychiatric, psychological, counselling, therapeutic, legal or financial service, and Listeners are not qualified professionals in those fields unless expressly stated.\n\nIf you are in distress, experiencing a mental health crisis, or at risk of harming yourself or others, please contact a qualified professional or an emergency helpline immediately. TrueLine is not an emergency service and cannot provide crisis intervention."
    )

    LegalSectionCard(
        number = "15 to 18",
        title = "Disclaimers, Liability & Governing Law",
        body = """15. Disclaimers:
The Platform is provided on an "as is" and "as available" basis. We do not warrant that the Platform will be uninterrupted or error-free.

16. Limitation of liability:
Our total aggregate liability in connection with the Platform will not exceed the total amount paid by you to TrueLine in the three (3) months preceding the event giving rise to the claim.

17. Indemnity:
You agree to indemnify and hold harmless Nexvert Technologies, its directors, employees and agents from any claim arising out of your breach of these Terms.

18. Governing law and disputes:
These Terms are governed by the laws of India. Courts at Bengaluru, Karnataka have exclusive jurisdiction."""
    )

    // Grievance Officer Card
    GrievanceOfficerCard()
}

@Composable
private fun PrivacyPolicyContent() {
    // Header Banner
    Surface(
        color = Color(0xFFF0FDF4),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Privacy Policy — TrueLine",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF166534)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Last updated: 7 August 2026  ·  Effective: 7 August 2026",
                fontSize = 12.5.sp,
                color = Color(0xFF16A34A),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "How Nexvert Technologies collects, uses and protects your personal data under the Digital Personal Data Protection Act, 2023 (DPDP Act) and the DPDP Rules, 2025.",
                fontSize = 13.sp,
                color = Color(0xFF1E293B),
                lineHeight = 18.sp
            )
        }
    }

    // Short Summary Box
    LegalCalloutBox(
        title = "The Short Version",
        content = "We collect your mobile number, basic profile details and how you use the app. Listeners additionally complete identity verification. We do not sell your data. We do not share your phone number with Listeners. You can delete your account, and your data, at any time from within the app."
    )

    LegalSectionCard(
        number = "1",
        title = "Who we are",
        body = """This Privacy Policy explains how Nexvert Technologies ("TrueLine", "we", "us") collects, uses, stores and protects your personal data when you use the TrueLine app or truelineapp.in.

For the purposes of the Digital Personal Data Protection Act, 2023 ("DPDP Act") and the Digital Personal Data Protection Rules, 2025, Nexvert Technologies is the Data Fiduciary and you are the Data Principal.

Registered office: 180/2, HSR Layout, Bengaluru, Karnataka 560068, India."""
    )

    LegalSectionCard(
        number = "2",
        title = "What we collect",
        body = """2.1 From all users:
• Mobile number: To create and secure your account, and sign you in via OTP.
• Display name, age confirmation, language preference: To set up your profile and match with Listeners.
• Coin balance and transaction history: To operate the wallet and bill calls accurately.
• Call metadata: Who you called, when, and call duration (not the audio conversation itself).
• Chat messages: Delivered on Platform; used to investigate reports of misconduct.
• Device and technical data: Device model, OS version, app version, IP address, crash logs.

2.2 DigiLocker & Aadhaar Policy:
For Listeners, identity verification is carried out through DigiLocker. We do not store your full Aadhaar number and do not store a copy of your Aadhaar document. We retain only the verification reference, verified name, and date of birth.

2.3 What we DO NOT collect:
• We do not collect your contact list.
• We do not collect precise GPS location.
• We do not require a profile photograph."""
    )

    LegalSectionCard(
        number = "3",
        title = "Why we collect it",
        bullets = listOf(
            "To provide the service — creating your account, connecting calls, delivering chats, operating the coin wallet.",
            "To verify identity — confirming Listeners are real, verified adults.",
            "To process payments and payouts — including tax deductions required by law.",
            "To keep the Platform safe — investigating reports, detecting fraud, enforcing our Terms.",
            "To support you — responding to your questions and complaints.",
            "To improve the app — diagnosing faults and understanding aggregate usage patterns.",
            "To meet legal obligations — including retention of financial records."
        ),
        body = "We do NOT sell your personal data. We do not use your data for third-party advertising."
    )

    LegalSectionCard(
        number = "4 & 5",
        title = "Calls and chats privacy",
        body = """Calls take place over the internet inside the app. Your phone number is never shared with a Listener, and a Listener's number is never shared with you.

Call recording:
Calls on TrueLine are recorded. Both people on the call are clearly told this at the start of every call, before the conversation begins.

We record calls for two reasons only:
1. Safety — so that reports of abuse or harassment can be properly investigated.
2. Dispute resolution — to resolve billing or service complaints fairly.

How recordings are handled:
• Recordings are encrypted and stored separately from profile data.
• Accessible only to authorised safety and support staff with logged access.
• Never shared with other users, Listeners, or advertisers. Never used to train AI models.
• Automatically deleted after 30 days, unless retained for an active dispute or legal requirement."""
    )

    LegalSectionCard(
        number = "6 & 7",
        title = "Data Sharing & Retention Schedule",
        body = """We share data only with essential processors bound by contract: Payment providers, Identity verification providers (DigiLocker), and Cloud hosting providers.

Retention periods:
• Account & profile data: Kept while account is active; deleted/anonymised after account deletion.
• Transaction & payment records: 8 years (statutory requirement under Indian tax/company law).
• Call metadata: 12 months.
• Chat messages: 12 months, or until account deletion.
• Call recordings: 30 days.
• Safety records: 3 years."""
    )

    LegalSectionCard(
        number = "8 & 9",
        title = "Data Protection & Your DPDP Rights",
        body = """Under the DPDP Act 2023, you have the right to:
• Access: Obtain a summary of personal data held about you.
• Correction & Updating: Have inaccurate or incomplete data corrected.
• Erasure: Have your personal data deleted, subject to legal retention requirements.
• Withdraw Consent: At any time from within Settings → Privacy.
• Grievance Redressal: Raise complaints with our Grievance Officer.
• Nominate: Nominate another person to exercise rights in the event of death/incapacity.

To exercise your rights, write to our Grievance Officer at admin@truelineapp.in. We respond within 7 working days."""
    )

    LegalSectionCard(
        number = "10 to 13",
        title = "Account Deletion, Children & Storage",
        body = """10. Deleting your account:
You can delete your account at any time directly in the app without needing email approval.

11. Children:
TrueLine is strictly an 18+ platform. We do not knowingly collect personal data from anyone under 18.

12. Data breaches:
In the event of a personal data breach, we will notify the Data Protection Board of India and affected users as mandated by the DPDP Act.

13. Storage and transfers:
Personal data is stored on secure servers located in India."""
    )

    // Grievance Officer Card
    GrievanceOfficerCard()
}

@Composable
private fun LegalSectionCard(
    number: String,
    title: String,
    body: String = "",
    bullets: List<String> = emptyList()
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = number,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            if (body.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = body,
                    fontSize = 13.5.sp,
                    color = Color(0xFF334155),
                    lineHeight = 20.sp
                )
            }

            if (bullets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                bullets.forEach { bullet ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", fontSize = 14.sp, color = Primary, fontWeight = FontWeight.Bold)
                        Text(
                            text = bullet,
                            fontSize = 13.5.sp,
                            color = Color(0xFF334155),
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalCalloutBox(title: String, content: String) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 13.5.sp,
                color = Color(0xFF334155),
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun GrievanceOfficerCard() {
    Surface(
        color = Color(0xFFFAF5FF),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF7E22CE),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Grievance Officer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF581C87)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "In accordance with the IT Act 2000, IT Rules 2021, and DPDP Act 2023:",
                fontSize = 13.sp,
                color = Color(0xFF6B21A8)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• Name: Akshaya Sinha\n• Designation: Grievance Officer, Nexvert Technologies\n• Email: admin@truelineapp.in\n• Address: 180/2, HSR Layout, Bengaluru, Karnataka 560068\n\nComplaints acknowledged within 24 hours and resolved within 15 days.",
                fontSize = 13.sp,
                color = Color(0xFF3B0764),
                lineHeight = 19.sp
            )
        }
    }
}
