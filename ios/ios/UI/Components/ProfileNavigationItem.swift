//
//  ProfileNavigationItem.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/6.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct ProfileNavigationItem: View {
    var body: some View {
        NavigationLink(destination: SettingsScreen()) {
            AvatarView(url: "https://avatars.githubusercontent.com/u/13329148?v=4", size: 32)
        }
    }
}

struct ProfileNavigationItem_Previews: PreviewProvider {
    static var previews: some View {
        ProfileNavigationItem()
    }
}
