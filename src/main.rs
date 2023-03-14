// Entry point for non-wasm
#[cfg(not(target_arch = "wasm32"))]
#[tokio::main]
#[allow(unused_mut)]
async fn main() {
    run().await;
}

use three_d::*;

#[allow(unused_mut)]
pub async fn run() {
    let window = Window::new(WindowSettings {
        title: "Texture!".to_string(),
        max_size: Some((1280, 720)),
        ..Default::default()
    })
        .unwrap();
    let context = window.gl();

    let cam_pos = vec3(-4.0, 4.0, 4.0);
    let look_at = vec3(0.0, 0.0, 0.0);
    let up = vec3(0.0, 1.0, 0.0);
    let mut camera = Camera::new_perspective(
        window.viewport(),
        cam_pos,
        look_at,
        up,
        degrees(45.0),
        0.1,
        1000.0,
    );
    let mut control = OrbitControl::new(*camera.target(), 1.0, 100.0);

    let mut loaded = three_d_asset::io::load_async(&[
        "media/bighead/bighead2.mtl",
        "media/bighead/bighead2.obj",
        "media/bighead/ooftex.png",
    ])
        .await
        .unwrap();

    let model = loaded.deserialize("bighead2.obj").unwrap();

    let  (mut pos_x, mut pos_y, mut pos_z) = (look_at.x, look_at.y, look_at.z);

    let mut oof_pos = vec3(pos_x, pos_y, pos_z);
    let mut oof = Model::<PhysicalMaterial>::new(&context, &model).unwrap();
    oof.iter_mut().for_each(|m| {
        m.set_transformation(Mat4::from_translation(oof_pos));
        m.material.render_states.cull = Cull::Back;
    });

    let ambient = AmbientLight::new(&context, 1.0, Color::WHITE);
    let directional = DirectionalLight::new(&context, 2.0, Color::WHITE, &vec3(0.0, -1.0, -1.0));

    let mut gui = GUI::new(&context);

    let mut rot = radians((0.0 * 0.005) as f32);
    let mut should_rotate = true;
    let mut time = 0.0;
    let mut rot_speed = 5.0;
    let mut panel_width = 0.0;

    // main loop
    window.render_loop(move |mut frame_input| {
        if should_rotate {
            time += rot_speed;
            rot = radians((time * 0.005) as f32).normalize();
        }

        let mut oof_pos = vec3(pos_x, pos_y, pos_z);
        let  (mut pos_x, mut pos_y, mut pos_z) = (oof_pos.x, oof_pos.y, oof_pos.z);
        let  (mut cam_x, mut cam_y, mut cam_z) = (camera.position().x, camera.position().z, camera.position().z);
        let  (mut cam_lookat_x, mut cam_lookat_y, mut cam_lookat_z) = (camera.target().x, camera.target().y, camera.target().z);

        gui.update(
            &mut frame_input.events,
            frame_input.accumulated_time,
            frame_input.viewport,
            frame_input.device_pixel_ratio,
            |gui_context| {
                use three_d::egui::*;
                SidePanel::left("side_panel").show(gui_context, |ui| {
                    ui.heading("Debug Panel");
                    ui.label("Camera Position");
                    ui.add(Slider::new(&mut cam_x, -10.0..=10.0).text("X"));
                    ui.add(Slider::new(&mut cam_y, -10.0..=10.0).text("Y"));
                    ui.add(Slider::new(&mut cam_z, -10.0..=10.0).text("Z"));

                    ui.label("Camera Orientation");
                    ui.add(Slider::new(&mut cam_lookat_x, -10.0..=10.0).text("X"));
                    ui.add(Slider::new(&mut cam_lookat_y, -10.0..=10.0).text("Y"));
                    ui.add(Slider::new(&mut cam_lookat_z, -10.0..=10.0).text("Z"));

                    ui.label("Object Position parameters");
                    ui.add(Slider::new(&mut pos_x, -100.0..=100.0).text("X"));
                    ui.add(Slider::new(&mut pos_y, -100.0..=100.0).text("Y"));
                    ui.add(Slider::new(&mut pos_z, -100.0..=100.0).text("Z"));

                    ui.label("Object Rotation");
                    ui.label(rot.0.to_string());

                    ui.checkbox(&mut should_rotate, "Rotating").clicked();
                });
                panel_width = gui_context.used_rect().width() as f64;
            },
        );

        //camera.set_view(vec3(cam_x, cam_y, cam_z), vec3(cam_lookat_x, cam_lookat_y, cam_lookat_z), up);

        oof_pos = vec3(pos_x, pos_y, pos_z);
        oof.iter_mut().for_each(|m| {
            m.set_transformation(Mat4::from_translation(oof_pos));
            m.set_transformation(Mat4::from_angle_y(rot));
        });

        let viewport = Viewport {
            x: (panel_width * frame_input.device_pixel_ratio) as i32,
            y: 0,
            width: frame_input.viewport.width
                - (panel_width * frame_input.device_pixel_ratio) as u32,
            height: frame_input.viewport.height,
        };

        camera.set_viewport(viewport);
        control.handle_events(&mut camera, &mut frame_input.events);

        // draw
        frame_input.screen()
            .clear(ClearState::color_and_depth(0.8, 0.8, 0.8, 1.0, 1.0))
            .render(&camera, oof.into_iter(), &[&ambient, &directional], )
            .write(|| gui.render());
        FrameOutput::default()
    });
}
