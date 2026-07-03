# re-bianpo Project Rules

Apply these rules whenever working in this repository.

## Repository Structure

- The repository root `re-bianpo` is the Java backend.
- Backend work is usually concentrated in the IoT module. When a backend task is not otherwise specified, look there first.

## Frontend Scope

- `bigscreen/` is the data bigscreen frontend.
- `rebianpo-ui/` is the admin frontend and uses Ant Design (`antd`).
- `rebianpo-uniapp/` is the UniApp mobile frontend, based on Vue 3 + TypeScript, and is used for H5, mini-program, and App multi-platform delivery.
- If a request mentions "frontend", "UI", "page", or "front-end project" without naming a directory, first determine whether it refers to `bigscreen/`, `rebianpo-ui/`, or `rebianpo-uniapp/` before editing code.
- If a request mentions "uniapp", "mobile", "mini-program", or "App" without naming a directory, check `rebianpo-uniapp/` first.

## IoT And Devices

- Only MQTT is enabled for device access. Do not assume other protocols are enabled.
- Use the project's custom raw device model and existing raw-device conventions unless the request explicitly requires a different model.

## Documentation Sync

- When making feature updates, logic changes, or technical refactors, consider whether `整体文档.md` should be updated in the same change.
- For very small changes, ask whether the user wants the document updated.
- Documentation updates should help a new maintainer quickly understand the system, setup, key configuration, and debugging approach.

## Project MCP

- A project MySQL MCP server named `mysql` is configured from the project's existing Cursor settings.
- Prefer that MCP when the task needs database schema inspection or safe read-oriented database investigation.
