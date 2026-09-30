import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Alert from "react-bootstrap/Alert";
import ProductItem from "./ProductItem";
export default function ProductList({ products, onEdit, onDelete }) {
    if (products.length === 0) {
        return <Alert variant="secondary">No products match the current filter.</Alert>;
    }
    return (
        <Row className="g-3">
            {products.map((product) => (
                <Col key={product.id} xs={12} md={6} xl={4}>
                    <ProductItem product={product} onEdit={onEdit} onDelete={onDelete}/>
                </Col>
            ))}
        </Row>
    );
}