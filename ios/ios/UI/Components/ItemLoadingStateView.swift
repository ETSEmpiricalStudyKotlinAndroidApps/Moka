//
//  ItemLoadingStateView.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/27.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIX
import common

struct ItemLoadingStateView: View {
    
    var status: Status
    
    var body: some View {
        HStack {
            Spacer()
            switch status {
            case Status.loading:
                ActivityIndicator()
                    .animated(true)
                    .style(.regular)
                    .frame(width: 40, height: 40)
            case Status.error:
                VStack {
                    Text(NSLocalizedString("Common.ErrorMessage", comment: ""))
                    Spacer()
                        .frame(width: 16, height: 16)
                    Button(action: {}) {
                        Text(NSLocalizedString("Common.Retry", comment: ""))
                    }
                }
            default:
                EmptyView()
            }
            Spacer()
        }
    }
    
}

struct ItemLoadingStateViewLoading_Previews: PreviewProvider {
    
    static var previews: some View {
        ItemLoadingStateView(status: .loading)
    }
    
}

struct ItemLoadingStateViewError_Previews: PreviewProvider {
    
    static var previews: some View {
        ItemLoadingStateView(status: .error)
    }
    
}
