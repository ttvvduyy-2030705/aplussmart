import SwiftUI

struct AplusRootView: View {
    @State private var route: String = "login"
    @State private var locks: [LockDevice] = [
        .init(id: "front-door", name: "Cửa chính", room: "Sảnh chính", isLocked: true, isOnline: true, battery: 86, signal: 92),
        .init(id: "office-door", name: "Phòng làm việc", room: "Tầng 2", isLocked: false, isOnline: true, battery: 64, signal: 78),
        .init(id: "hotel-203", name: "Phòng 203", room: "Khách sạn", isLocked: true, isOnline: false, battery: 24, signal: 35),
    ]

    var body: some View {
        NavigationStack {
            ZStack {
                LinearGradient(colors: [Color.black, Color(red: 0.08, green: 0.01, blue: 0.02), Color.black], startPoint: .top, endPoint: .bottom)
                    .ignoresSafeArea()
                if route == "login" {
                    loginView
                } else if route == "home_dashboard" {
                    homeView
                } else {
                    featureView(spec: aplusScreens.first { $0.route == route } ?? aplusScreens[3])
                }
            }
        }
    }

    private var loginView: some View {
        VStack(spacing: 18) {
            logo
            AplusPanel {
                Text("Aplus Lock").font(.system(size: 34, weight: .black)).foregroundColor(.white)
                Text("Đăng nhập hệ thống khóa thông minh").font(.system(size: 17)).foregroundColor(.white.opacity(0.68))
                Button("Đăng nhập demo") { route = "home_dashboard" }.buttonStyle(AplusButtonStyle())
            }
        }.padding(22)
    }

    private var homeView: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Aplus Lock").font(.system(size: 34, weight: .black)).foregroundColor(.white)
                Text("31 màn theo UI reference").foregroundColor(.white.opacity(0.65))
                ForEach(locks) { lock in
                    AplusPanel {
                        Text(lock.name).font(.system(size: 22, weight: .bold)).foregroundColor(.white)
                        Text("\(lock.room) • \(lock.isOnline ? "Online" : "Offline") • Pin \(lock.battery)%").foregroundColor(.white.opacity(0.65))
                    }
                }
                LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                    ForEach(aplusScreens.dropFirst(4)) { spec in
                        Button { route = spec.route } label: {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("#\(String(format: "%02d", spec.id))").foregroundColor(.red)
                                Text(spec.title).font(.system(size: 16, weight: .bold)).foregroundColor(.white).multilineTextAlignment(.leading)
                                Text(spec.referenceImage).font(.caption).foregroundColor(.white.opacity(0.45))
                            }.frame(maxWidth: .infinity, alignment: .leading).padding(14).background(Color.white.opacity(0.06)).clipShape(RoundedRectangle(cornerRadius: 20))
                        }
                    }
                }
            }.padding(18)
        }
    }

    private func featureView(spec: ScreenSpec) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Button("‹ Quay lại") { route = "home_dashboard" }.foregroundColor(.white.opacity(0.7))
                Text(spec.title).font(.system(size: 31, weight: .black)).foregroundColor(.white)
                Text(spec.subtitle).foregroundColor(.white.opacity(0.65))
                AplusPanel {
                    Text("Reference: \(spec.referenceImage)").foregroundColor(.white.opacity(0.55))
                    Text("Screen route: \(spec.route)").font(.system(size: 19, weight: .bold)).foregroundColor(.white)
                    Button("Thực thi mock action") {}.buttonStyle(AplusButtonStyle())
                }
            }.padding(18)
        }
    }

    private var logo: some View {
        VStack(spacing: 10) {
            RoundedRectangle(cornerRadius: 26).fill(LinearGradient(colors: [.red, Color(red: 0.45, green: 0, blue: 0.04)], startPoint: .top, endPoint: .bottom)).frame(width: 88, height: 88).overlay(Text("A").font(.system(size: 42, weight: .black)).foregroundColor(.white))
            Text("APLUS").font(.system(size: 34, weight: .black)).foregroundColor(.white)
        }
    }
}

struct AplusPanel<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View {
        VStack(alignment: .leading, spacing: 12) { content }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(18)
            .background(Color.white.opacity(0.07))
            .clipShape(RoundedRectangle(cornerRadius: 24))
            .overlay(RoundedRectangle(cornerRadius: 24).stroke(Color.white.opacity(0.12)))
    }
}

struct AplusButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 17, weight: .bold))
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 15)
            .background(Color.red.opacity(configuration.isPressed ? 0.65 : 1))
            .clipShape(RoundedRectangle(cornerRadius: 18))
    }
}
