//
//  EmptyScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/1.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct EmptyScreen: View {
    
    let msgString: String
    let actionString: String
    let action: () -> Void
    
    init(
        msgString: String,
        actionString: String,
        action: @escaping () -> Void
    ) {
        self.msgString = msgString
        self.actionString = actionString
        self.action = action
    }
    
    init(action: @escaping () -> Void) {
        self.msgString = NSLocalizedString("Common.ErrorMessage", comment: "")
        self.actionString = NSLocalizedString("Common.Retry", comment: "")
        self.action = action
    }
    
    var body: some View {
        VStack(
            alignment: .center,
            spacing: 10
        ) {
            Spacer()
            Text(msgString)
            Button(actionString) {
                self.action()
            }
            Spacer()
        }
    }
    
}

struct EmptyScreen_Previews: PreviewProvider {
    
    static var previews: some View {
        EmptyScreen() {}
    }
    
}
