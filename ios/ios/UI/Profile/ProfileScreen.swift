//
//  ProfileScreen.swift
//  ios
//
//  Created by lizhaotailang on 2021/10/31.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import common

struct ProfileScreen: View {
    
    var login: String
    var profileType: ProfileType
    
    @ObservedObject var viewModel: ProfileViewModel
    
    init(login: String, profileType: ProfileType) {
        self.login = login
        self.profileType = profileType
        self.viewModel = ProfileViewModel(login: login, profileType: profileType)
    }
    
    var body: some View {
        Text("")
    }
    
}
