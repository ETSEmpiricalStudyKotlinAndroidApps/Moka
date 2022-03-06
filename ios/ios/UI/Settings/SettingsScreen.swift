//
//  SettingsScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/6.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SettingsScreen: View {
    
    var body: some View {
        NavigationLink(destination: GitHubStatusScreen()) {
            Text(/*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/)
        }
        .navigationTitle("Settings")
    }
    
}

struct SettingsScreen_Previews: PreviewProvider {
    static var previews: some View {
        SettingsScreen()
    }
}
