//
//  Avatar.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/28.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Kingfisher

struct AvatarView: View {
    
    private let url: String
    private let size: CGFloat?
    private let padding: CGFloat?
    
    init(url: String?) {
        self.init(
            url: url,
            size: 40
        )
    }
    
    init(
        url: String?,
        size: CGFloat?,
        padding: CGFloat? = 4
    ) {
        self.url = url.ifNullOrEmpty {
            "https://avatars.githubusercontent.com/u/10137"
        }
        self.size = size
        self.padding = padding
    }
    
    var body: some View {
        KFImage.url(URL(string: url)!)
            .placeholder {
                Color.gray
            }
            .fade(duration: 0.25)
            .resizable()
            .aspectRatio(contentMode: .fill)
            .clipShape(Circle())
            .padding(padding ?? 0)
            .frame(width: size, height: size, alignment: .center)
            
    }
    
}
