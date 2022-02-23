//
//  ProfileViewModel.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/24.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import common

class ProfileViewModel: ObservableObject {
    
    let login: String
    let profileType: ProfileType
    
    init(login: String, profileType: ProfileType) {
        self.login = login
        self.profileType = profileType
    }
    
}
