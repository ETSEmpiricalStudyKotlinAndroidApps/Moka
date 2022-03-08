//
//  SettingsOptionItem.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/8.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SettingsOptionItem: View {
    
    let imageSystemName: String
    let imageBgColor: Color
    let textString: String
    let trailingTextString: String?
    let disclosureIndicator: Bool
    
    init(
        imageSystemName: String,
        imageBgColor: Color,
        textString: String,
        trailingTextString: String? = nil,
        disclosureIndicator: Bool = false
    ) {
        self.imageSystemName = imageSystemName
        self.imageBgColor = imageBgColor
        self.textString = textString
        self.trailingTextString = trailingTextString
        self.disclosureIndicator = disclosureIndicator
    }
    
    var body: some View {
        HStack(
            alignment: .center,
            spacing: 20
        ) {
            ZStack {
                Image(systemName: imageSystemName)
                    .foregroundColor(.white)
                    .font(.system(size: 14))
            }
            .frame(width: 28, height: 28, alignment: .center)
            .background(imageBgColor)
            .clipShape(RoundedRectangle(cornerRadius: 6))
            
            Text(textString)
                .foregroundColor(.black)
                .font(.body)
            
            Spacer()
            
            if trailingTextString != nil {
                Text(trailingTextString!)
                    .foregroundColor(.systemGray)
                    .font(.body)
            } else {
                EmptyView()
            }
            
            if disclosureIndicator {
                Image(systemName: "chevron.forward")
                    .font(Font.system(.caption2).weight(.bold))
                    .foregroundColor(Color(UIColor.tertiaryLabel))
            } else {
                EmptyView()
            }
        }
        .contentShape(Rectangle())
        .padding(.vertical, 2)
    }
}
