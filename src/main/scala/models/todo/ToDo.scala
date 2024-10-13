package models.todo

import java.time.Instant

final case class ToDo(
    id: String,
    description: String,
    status: Status,
    createdAt: Instant
)
