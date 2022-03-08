//
//  SettingsScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/6.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SettingsScreen: View {
    @Binding var presentSelf: Bool
    
    var body: some View {
        NavigationView {
            List {
                Section {
                    AccountHeaderView()
                }
                Section {
                    NavigationLink(destination: ManageAccountsScreen()) {
                        SettingsOptionItem(
                            imageSystemName: "person.crop.circle.fill",
                            imageBgColor: .systemBlue,
                            textString: NSLocalizedString("Settings.ManageAccounts", comment: "")
                        )
                    }
                    NavigationLink(destination: AppearanceSettingsScreen()) {
                        SettingsOptionItem(
                            imageSystemName: "moon.stars.fill",
                            imageBgColor: .systemTeal,
                            textString: NSLocalizedString("Settings.Appearance", comment: ""),
                            trailingTextString: NSLocalizedString("Settings.Appearance.Auto", comment: "")
                        )
                    }
                    NavigationLink(destination: NotificationSettingsScreen()) {
                        SettingsOptionItem(
                            imageSystemName: "bell.fill",
                            imageBgColor: .cyan,
                            textString: NSLocalizedString("Settings.Notifications", comment: "")
                        )
                    }
                }
                Section {
                    NavigationLink(destination: GitHubStatusScreen()) {
                        SettingsOptionItem(
                            imageSystemName: "server.rack",
                            imageBgColor: .green,
                            textString: NSLocalizedString("GitHubStatus", comment: "")
                        )
                    }
                }
                Section {
                    SettingsOptionItem(
                        imageSystemName: "newspaper.fill",
                        imageBgColor: .systemIndigo,
                        textString: NSLocalizedString("Settings.Newsletters", comment: ""),
                        disclosureIndicator: true
                    )
                    NavigationLink(destination: ProfileScreen(login: "TonnyL", profileType: .user)) {
                        SettingsOptionItem(
                            imageSystemName: "person.fill",
                            imageBgColor: .systemYellow,
                            textString: NSLocalizedString("Settings.Author", comment: ""),
                            trailingTextString: "@TonnyL"
                        )
                    }
                    SettingsOptionItem(
                        imageSystemName: "text.bubble.fill",
                        imageBgColor: .systemPurple,
                        textString: NSLocalizedString("Settings.Feedback", comment: ""),
                        disclosureIndicator: true
                    )
                }
                Section {
                    SettingsOptionItem(
                        imageSystemName: "doc.fill",
                        imageBgColor: .systemOrange,
                        textString: NSLocalizedString("Settings.TermsOfService", comment: ""),
                        disclosureIndicator: true
                    )
                    .onTapGesture {
                        UIApplication.shared.open(URL(string: "https://tonnyl.github.io/android/terms-conditions.html")!)
                    }
                    SettingsOptionItem(
                        imageSystemName: "hand.raised.fill",
                        imageBgColor: .systemGray,
                        textString: NSLocalizedString("Settings.PrivacyPolicy", comment: ""),
                        disclosureIndicator: true
                    )
                        .onTapGesture {
                            UIApplication.shared.open(URL(string: "https://tonnyl.github.io/android/privacy-policy.html")!)
                        }
                    SettingsOptionItem(
                        imageSystemName: "info.circle.fill",
                        imageBgColor: .systemBlue,
                        textString: NSLocalizedString("FAQs & Help", comment: ""),
                        disclosureIndicator: true
                    )
                        .buttonStyle(.automatic)
                        .onTapGesture {
                            UIApplication.shared.open(URL(string: "https://tonnyl.github.io/guide/")!)
                        }
                }
                Section {
                    SettingsOptionItem(
                        imageSystemName: "tag.fill",
                        imageBgColor: .systemPink,
                        textString: NSLocalizedString("Settings.Versions", comment: ""),
                        trailingTextString: Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String
                    )
                    SettingsOptionItem(
                        imageSystemName: "heart.fill",
                        imageBgColor: .systemGray2,
                        textString: NSLocalizedString("Settings.OpenSourceLicense", comment: ""),
                        disclosureIndicator: true
                    )
                        .onTapGesture {
                            UIApplication.shared.open(URL(string: "https://tonnyl.github.io/iOS/open-source-licenses.html")!)
                        }
                    SettingsOptionItem(
                        imageSystemName: "bell.badge",
                        imageBgColor: .systemRed,
                        textString: NSLocalizedString("Settings.WhatsNew", comment: ""),
                        disclosureIndicator: true
                    )
                }
            }
            .navigationTitle(NSLocalizedString("Settings", comment: ""))
            .navigationBarItems(
                trailing: Button(
                    action: {
                        self.presentSelf = false
                    }
                ) {
                    Text(NSLocalizedString("Common.Done", comment: ""))
                }
            )
        }
    }
    
}

struct AccountHeaderView: View {
    
    var body: some View {
        NavigationLink(destination: ProfileScreen(login: "TonnyL", profileType: .user)) {
            HStack(
                alignment: .center,
                spacing: 20
            ) {
                AvatarView(
                    url: "https://avatars.githubusercontent.com/u/13329148?v=4",
                    size: 56
                )
                VStack(
                    alignment: .leading,
                    spacing: 8
                ) {
                    Text("Li Zhao Tai Lang")
                        .font(.headline)
                        .foregroundColor(.black)
                    Text("TonnyL")
                        .font(.body)
                }
                Spacer()
            }
            .padding(.vertical, 10)
        }
    }
    
}

#if DEBUG
struct SettingsScreen_Previews: PreviewProvider {
    
    static var previews: some View {
        SettingsScreen(presentSelf: .constant(true))
    }
    
}
#endif
