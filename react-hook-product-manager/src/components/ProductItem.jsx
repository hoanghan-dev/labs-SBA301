import Button from "react-bootstrap/Button";
import Card from "react-bootstrap/Card";
const money = new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND"
});
export default function ProductItem({ product, onEdit, onDelete }) {
    return (
        <Card className="h-100 shadow-sm">
            <Card.Body className="d-flex flex-column">
                <Card.Title>{product.name}</Card.Title>
                <Card.Subtitle className="mb-2 text-secondary">
                    {product.category}
                </Card.Subtitle>
                <div className="mb-1">Price: {money.format(product.price)}</div>
                <div className="mb-3">Quantity: {product.quantity}</div>
                <div className="mt-auto d-flex gap-2">
                    <Button size="sm" variant="warning" onClick={() => onEdit(product)}>
                        Edit
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => onDelete(product.id)}>
                        Delete
                    </Button>
                </div>
            </Card.Body>
        </Card>
    );
}