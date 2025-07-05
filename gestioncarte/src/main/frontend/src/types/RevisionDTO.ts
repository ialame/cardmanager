export interface RevisionDTO<T> {
    number?: number;
    modificationDate?: Date;
    type?: "INSERT" | "UPDATE" | "DELETE";
    author?: string;
    message?: string;
    data?: T;
}
