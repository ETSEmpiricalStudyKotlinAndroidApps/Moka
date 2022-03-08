//
//  ProfileNavigationItem.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/6.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SettingsNavigationItem: View {
    @State var isActive: Bool = false
    
    var body: some View {
        AvatarView(
            url: "https://avatars.githubusercontent.com/u/13329148?v=4",
            size: 40
        )
        .onTapGesture {
            isActive = true
        }
        .sheet(isPresented: $isActive) {
            SettingsScreen(presentSelf: $isActive)
                .onDisappear {
                    isActive = false
                }
        }
    }
}

struct ProfileNavigationItem_Previews: PreviewProvider {
    static var previews: some View {
        SettingsNavigationItem()
    }
}
