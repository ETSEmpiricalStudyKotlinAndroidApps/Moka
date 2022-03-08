//
//  AppearanceSettingsScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/8.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

enum Appearance {
    
    case auto
    
    case light
    
    case dark
    
}

extension Appearance {
    
    var text: String {
        switch self {
        case .auto:
            return NSLocalizedString("Settings.Appearance.Auto", comment: "")
        case .light:
            return NSLocalizedString("Settings.Appearance.Light", comment: "")
        case .dark:
            return NSLocalizedString("Settings.Appearance.Dark", comment: "")
        }
    }
    
}

struct AppearanceSettingsScreen: View {
    
    @State private var selected: Appearance = .auto
    
    var body: some View {
        List {
            Section {
                AppearanceSettingsOption(
                    appearance: .auto,
                    selected: selected
                )
                AppearanceSettingsOption(
                    appearance: .light,
                    selected: selected
                )
                AppearanceSettingsOption(
                    appearance: .dark,
                    selected: selected
                )
            }
        }
        .navigationTitle(NSLocalizedString("Settings.Appearance", comment: ""))
    }
}

struct AppearanceSettingsOption: View {
    
    let appearance: Appearance
    @State var selected: Appearance
    
    var body: some View {
        HStack {
            Text(NSLocalizedString(appearance.text, comment: ""))
            Spacer()
            Image(systemName: self.selected == appearance ? "checkmark.circle.fill" : "circle")
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundColor(self.selected == appearance ? .blue : .gray)
                .font(.system(size: 20, weight: .regular, design: .default))
                .onTapGesture {
                    selected = appearance
                }
        }
        .padding(.vertical, 2)
    }
    
}

struct AppearanceSettingsScreen_Previews: PreviewProvider {
    static var previews: some View {
        AppearanceSettingsScreen()
    }
}
