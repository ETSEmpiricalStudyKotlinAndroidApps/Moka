//
//  TrendingDeveloperRowView.swift
//  ios
//
//  Created by lizhaotailang on 2021/10/30.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct TrendingDeveloperRowView: View {
    var developer: TrendingDeveloper
    var position: Int
    
    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text("\(position)")
            VStack(alignment: .leading, spacing: 5) {
                Text("\(developer.username)(\(developer.username))")
                Text("\(developer.repository.name) - \(developer.repository.description ?? "")")
            }
        }.frame(width: 300, height: 200, alignment: .leading)
    }
    
}

#if DEBUG
struct TrendingDeveloperRow_Previews: PreviewProvider {
    static let trendingDeveloper = TrendingDeveloper(username: "google", name: "Google", url: "https://github.com/google", avatar: "https://avatars0.githubusercontent.com/u/1342004", repository: TrendingDeveloperRepository(name: "traceur-compiler", description: "Traceur is a JavaScript.next-to-JavaScript-of-today compiler", url: "https://github.com/google/traceur-compiler"))
    
    static var previews: some View {
        TrendingDeveloperRowView(developer: trendingDeveloper, position: 1)
    }
}
#endif
